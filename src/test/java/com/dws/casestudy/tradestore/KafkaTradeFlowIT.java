package com.dws.casestudy.tradestore;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dws.casestudy.tradestore.entity.TradeId;
import com.dws.casestudy.tradestore.repository.TradeRepository;

@SpringBootTest
public class KafkaTradeFlowIT extends AbstractIT {

    @Autowired
    TradeRepository repo;

    @Test
    void kafkaMessageShouldPersistTrade() throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
			String json = """
					{
					      "tradeId":"KAFKA-T1",
					      "version":1,
					      "counterPartyId":"CP-K",
					      "bookId":"B-K",
					      "maturityDate":"%s",
					      "expired":false
			        }
			        """.formatted(LocalDate.now().plusDays(3));

			producer.send(new ProducerRecord<>("trades", "key1", json));
			producer.flush();
		}

        // Wait for consumer to process
        sleep(2000);

        var saved = repo.findById(new TradeId("KAFKA-T1", 1));
        assertThat(saved).isPresent();
        assertThat(saved.get().getCounterPartyId()).isEqualTo("CP-K");
    }
}
