package com.dws.casestudy.tradestore.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.entity.IdempotencyKey;
import com.dws.casestudy.tradestore.service.IdempotencyService;
import com.dws.casestudy.tradestore.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TradeKafkaListenerTest {

    private TradeService tradeService;
    private IdempotencyService idempotencyService;
    private TradeKafkaListener listener;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
    	mapper.registerModule(new JavaTimeModule());
        tradeService = Mockito.mock(TradeService.class);
        idempotencyService = Mockito.mock(IdempotencyService.class);
        listener = new TradeKafkaListener(tradeService, idempotencyService, mapper);
    }

    @Test
    void repeatedMessageShouldBeIgnored() throws Exception {
        when(idempotencyService.get("kafka:abc")).thenReturn(Optional.of(new IdempotencyKey()));

        listener.consume(new ConsumerRecord<>("trades", 0, 0, "abc", "{}"));

        verify(tradeService, never()).createOrReplace(any());
    }

    @Test
    void validMessageShouldProcess() throws Exception {
        TradeDto dto = new TradeDto("T1", 1, "CP", "B1", LocalDate.now().plusDays(3), null, false);
        String json = mapper.writeValueAsString(dto);

        when(idempotencyService.get(any())).thenReturn(Optional.empty());

        listener.consume(new ConsumerRecord<>("trades", 0, 0, "abc", json));
        verify(tradeService, times(1)).createOrReplace(any());
    }
}
