package com.dws.casestudy.tradestore.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.service.IdempotencyService;
import com.dws.casestudy.tradestore.service.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TradeKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(TradeKafkaListener.class);
    private final TradeService tradeService;
	private final IdempotencyService idempotencyService;
	private final ObjectMapper objectMapper;

    public TradeKafkaListener(TradeService tradeService, IdempotencyService idempotencyService, ObjectMapper mapper) {
        this.tradeService = tradeService;
		this.idempotencyService = idempotencyService;
		objectMapper = mapper;
    }

    @KafkaListener(topics = "trades", groupId = "trade-store-group")
    public void consume(ConsumerRecord<String, String> record) {
        String key = record.key();
        String body = record.value();

        String idKey = key != null ? "kafka:" + key : "kafka-hash:" + Integer.toHexString(body.hashCode());

        var existing = idempotencyService.get(idKey);
        if (existing.isPresent()) {
            log.info("Kafka idempotent replay ignored for key {}", idKey);
            return;
        }

        try {
            TradeDto dto = objectMapper.readValue(body, TradeDto.class);
            tradeService.createOrReplace(dto);
            idempotencyService.save(idKey, 200, body);
        } catch (Exception e) {
            log.error("Kafka processing failed", e);
            idempotencyService.save(idKey, 500, e.getMessage());
        }
    }
}
