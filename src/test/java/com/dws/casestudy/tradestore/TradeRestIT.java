package com.dws.casestudy.tradestore;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dws.casestudy.tradestore.dto.TradeDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TradeRestIT extends AbstractIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void restShouldPersistTrade() {
        String url = "http://localhost:" + port + "/api/v1/trades";

        TradeDto dto = new TradeDto("REST-T1", 1, "CP-R", "B-R", LocalDate.now().plusDays(5), null, false);

        ResponseEntity<TradeDto> response = rest.postForEntity(url, dto, TradeDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTradeId()).isEqualTo("REST-T1");
    }
}
