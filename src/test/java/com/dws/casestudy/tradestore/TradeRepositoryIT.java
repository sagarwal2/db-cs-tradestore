package com.dws.casestudy.tradestore;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.dws.casestudy.tradestore.entity.Trade;
import com.dws.casestudy.tradestore.repository.TradeRepository;

@SpringBootTest
@Transactional
public class TradeRepositoryIT extends AbstractIT {

    @Autowired
    TradeRepository repo;

    @Test
    void shouldInsertAndFetchLatest() {
        Trade t1 = Trade.of("T100", 1);
        t1.setMaturityDate(LocalDate.now().plusDays(10));
        t1.setCreatedDate(LocalDateTime.now());
        repo.save(t1);

        Trade t2 = Trade.of("T100", 2);
        t2.setMaturityDate(LocalDate.now().plusDays(20));
        t2.setCreatedDate(LocalDateTime.now());
        repo.save(t2);

        var latest = repo.findTopByTradeIdForUpdate("T100");
        assertThat(latest).isPresent();
        assertThat(latest.get().getId().getVersion()).isEqualTo(2);
    }
}
