package com.dws.casestudy.tradestore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.service.IdempotencyService;
import com.dws.casestudy.tradestore.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(TradeController.class)
public class TradeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TradeService service;

    @MockBean
    IdempotencyService idempotencyService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
    	mapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void createTrade_shouldReturn201() throws Exception {
        Mockito.when(service.createOrReplace(any()))
                .thenReturn(new TradeService.ServiceResult(true,
                        new TradeDto("T1", 1, "CP", "B1", LocalDate.now().plusDays(3), null, false)));

        TradeDto dto = new TradeDto("T1", 1, "CP", "B1", LocalDate.now().plusDays(3), null, false);

        mvc.perform(post("/api/v1/trades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
