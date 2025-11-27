package com.dws.casestudy.tradestore.dto;

import static java.util.Objects.requireNonNullElse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dws.casestudy.tradestore.entity.Trade;
import com.dws.casestudy.tradestore.entity.TradeId;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {
    @NotBlank
    private String tradeId;
    @NotNull
    private Integer version;
    private String counterPartyId;
    private String bookId;
    @NotNull
    private LocalDate maturityDate;
    private LocalDateTime createdDate;
    private boolean expired;

    public Trade toEntity() {
        Trade t = new Trade();
        t.setId(new TradeId(tradeId, version));
        t.setCounterPartyId(counterPartyId);
        t.setBookId(bookId);
        t.setMaturityDate(maturityDate);
        t.setCreatedDate(requireNonNullElse(createdDate, LocalDateTime.now()));
        t.setExpired(expired);
        return t;
    }

    public static TradeDto fromEntity(Trade t) {
        return new TradeDto(
                t.getId().getTradeId(),
                t.getId().getVersion(),
                t.getCounterPartyId(),
                t.getBookId(),
                t.getMaturityDate(),
                t.getCreatedDate(),
                t.isExpired()
        );
    }
}
