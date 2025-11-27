package com.dws.casestudy.tradestore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.entity.IdempotencyKey;
import com.dws.casestudy.tradestore.service.IdempotencyService;
import com.dws.casestudy.tradestore.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService service;
    private final IdempotencyService idempotencyService;
    @Autowired
    private ObjectMapper objectMapper;

    public TradeController(TradeService service, IdempotencyService idempotencyService) {
        this.service = service;
        this.idempotencyService = idempotencyService;
    }

    // ======================================================
    // 1. CREATE / REPLACE TRADE
    // ======================================================

    @Operation(
            summary = "Create or replace a trade",
            description = """
                    Creates a new trade (201) or replaces an existing trade with the same version (200).
                    Rejects lower versions (409) or past maturity dates (422).
                    Idempotency key ensures safe retries.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TradeDto.class),
                            examples = @ExampleObject("""
                                    {
                                      "tradeId": "T2",
                                      "version": 2,
                                      "counterPartyId": "CP-2",
                                      "bookId": "B1",
                                      "maturityDate": "2025-05-20",
                                      "expired": false
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created new trade",
                            content = @Content(schema = @Schema(implementation = TradeDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "200",
                            description = "Replaced existing trade of same version",
                            content = @Content(schema = @Schema(implementation = TradeDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Lower version rejected",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject("""
                                            {
                                              "errorCode": "LOWER_VERSION",
                                              "message": "incoming version 1 is lower than stored latest version 3"
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Maturity date invalid",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject("""
                                            {
                                              "errorCode": "MATURITY_IN_PAST",
                                              "message": "maturityDate 2020-01-01 is before today"
                                            }
                                            """))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TradeDto> createTrade(
            @Parameter(description = "Idempotency key for safe retries", example = "req-1234")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TradeDto dto) throws Exception {

        if (idempotencyKey != null) {
            var existing = idempotencyService.get(idempotencyKey);
            if (existing.isPresent()) {
                IdempotencyKey k = existing.get();
                TradeDto previous = objectMapper.readValue(k.getResponseBody(), TradeDto.class);
                return ResponseEntity.status(k.getStatusCode()).body(previous);
            }
        }

        var result = service.createOrReplace(dto);
        ResponseEntity<TradeDto> response = ResponseEntity
                .status(result.isCreated() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(result.getDto());

        if (idempotencyKey != null) {
            idempotencyService.save(
                    idempotencyKey,
                    response.getStatusCode().value(),
                    objectMapper.writeValueAsString(result.getDto())
            );
        }

        return response;
    }

    // ======================================================
    // 2. GET LATEST VERSION
    // ======================================================

    @Operation(
            summary = "Get latest version of a trade",
            description = "Returns the most recent version of a trade.",
            parameters = {
                    @Parameter(name = "tradeId", example = "T1")
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Latest trade",
                            content = @Content(schema = @Schema(implementation = TradeDto.class))),
                    @ApiResponse(responseCode = "404", description = "Trade not found")
            }
    )
    @GetMapping("/{tradeId}")
    public ResponseEntity<TradeDto> getLatest(@PathVariable String tradeId) {
        return service.getLatest(tradeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ======================================================
    // 3. GET SPECIFIC VERSION
    // ======================================================

    @Operation(
            summary = "Get a specific version of a trade",
            parameters = {
                    @Parameter(name = "tradeId", example = "T1"),
                    @Parameter(name = "version", example = "2")
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Trade version",
                            content = @Content(schema = @Schema(implementation = TradeDto.class))),
                    @ApiResponse(responseCode = "404", description = "Version not found")
            }
    )
    @GetMapping("/{tradeId}/versions/{version}")
    public ResponseEntity<TradeDto> getVersion(
            @PathVariable String tradeId,
            @PathVariable Integer version) {
        return service.getByVersion(tradeId, version)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ======================================================
    // 4. SEARCH + PAGINATION
    // ======================================================

    @Operation(
            summary = "Search trades with pagination & filtering",
            description = "Supports filtering by counterPartyId and expired flag, as well as paging & sorting.",
            parameters = {
                    @Parameter(name = "page", example = "0"),
                    @Parameter(name = "size", example = "20"),
                    @Parameter(name = "sort", example = "createdDate,desc"),
                    @Parameter(name = "counterPartyId", example = "CP-1"),
                    @Parameter(name = "expired", example = "false")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Paged result of trades",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                      "content": [
                                        {
                                          "tradeId": "T1",
                                          "version": 1,
                                          "counterPartyId": "CP-1",
                                          "bookId": "B1",
                                          "maturityDate": "2025-12-31",
                                          "createdDate": "2025-11-22T10:15:30",
                                          "expired": false
                                        }
                                      ],
                                      "totalElements": 1,
                                      "totalPages": 1
                                    }
                                    """)
                    )
            )
    )
    @GetMapping
    public ResponseEntity<Page<TradeDto>> searchTrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdDate,desc") String[] sort,
            @RequestParam(required = false) String counterPartyId,
            @RequestParam(required = false) Boolean expired) {

        Sort sortSpec = Sort.by(
                Sort.Order.by(sort[0]).with(
                        sort.length > 1 && "asc".equalsIgnoreCase(sort[1])
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC));

        Pageable pageable = PageRequest.of(page, size, sortSpec);
        Page<TradeDto> result = service.searchTrades(counterPartyId, expired, pageable);

        return ResponseEntity.ok(result);
    }
}
