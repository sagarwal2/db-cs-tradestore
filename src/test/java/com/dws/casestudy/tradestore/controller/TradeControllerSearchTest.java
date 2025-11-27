package com.dws.casestudy.tradestore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.service.IdempotencyService;
import com.dws.casestudy.tradestore.service.TradeService;

@WebMvcTest(TradeController.class)
class TradeControllerSearchTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TradeService service;

    @MockBean
    IdempotencyService idempotencyService;

    @Test
    void searchTrades_shouldReturnPage() throws Exception {
        TradeDto dto = new TradeDto("T1", 1, "CP-1", "B1", LocalDate.now().plusDays(3), null, false);
        Page<TradeDto> page = new PageImpl<>(List.of(dto));

        Mockito.when(service.searchTrades(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(page);

        mvc.perform(get("/api/v1/trades")
                .param("page", "0")
                .param("size", "20")
                .param("counterPartyId", "CP-1")
                .param("expired", "false"))
                .andExpect(status().isOk());
    }
}
