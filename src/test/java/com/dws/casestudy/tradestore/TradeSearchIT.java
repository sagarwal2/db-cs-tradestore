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

import com.dws.casestudy.tradestore.entity.Trade;
import com.dws.casestudy.tradestore.repository.TradeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TradeSearchIT extends AbstractIT {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    TradeRepository repo;

    @LocalServerPort
    int port;

    @Test
    void searchTrades_shouldReturnFilteredPage() {
        Trade t = Trade.of("S1", 1);
        t.setCounterPartyId("CP-1");
        t.setBookId("B1");
        t.setMaturityDate(LocalDate.now().plusDays(5));
        t.setCreatedDate(java.time.LocalDateTime.now());
        t.setExpired(false);
        repo.save(t);

        String url = "http://localhost:" + port + "/api/v1/trades?counterPartyId=CP-1&expired=false";

        ResponseEntity<String> resp = rest.getForEntity(url, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).contains("CP-1");
    }
}
