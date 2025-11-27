package com.dws.casestudy.tradestore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.entity.Trade;
import com.dws.casestudy.tradestore.exception.LowerVersionException;
import com.dws.casestudy.tradestore.exception.MaturityInPastException;
import com.dws.casestudy.tradestore.repository.TradeRepository;

public class TradeServiceTest {

    private TradeRepository repo;
    private TradeService service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(TradeRepository.class);
        service = new TradeService(repo);
    }

    @Test
    void createNewTrade_shouldCreate() {
        TradeDto dto = new TradeDto("T1", 1, "CP", "B1", LocalDate.now().plusDays(5), null, false);

        when(repo.findTopByTradeIdForUpdate("T1")).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var res = service.createOrReplace(dto);
        assertThat(res.isCreated()).isTrue();
    }

    @Test
    void lowerVersionShouldThrow() {
        Trade existing = Trade.of("T1", 2);
        existing.setCreatedDate(LocalDateTime.now());

        when(repo.findTopByTradeIdForUpdate("T1")).thenReturn(Optional.of(existing));

        TradeDto dto = new TradeDto("T1", 1, "CP", "B1", LocalDate.now().plusDays(5), null, false);
        assertThatThrownBy(() -> service.createOrReplace(dto))
                .isInstanceOf(LowerVersionException.class);
    }

    @Test
    void sameVersionShouldReplace() {
        Trade existing = Trade.of("T1", 1);
        existing.setCreatedDate(LocalDateTime.now());

        when(repo.findTopByTradeIdForUpdate("T1")).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TradeDto dto = new TradeDto("T1", 1, "CP2", "B2", LocalDate.now().plusDays(5), null, false);
        var res = service.createOrReplace(dto);
        assertThat(res.isCreated()).isFalse();
        assertThat(res.getDto().getCounterPartyId()).isEqualTo("CP2");
    }

    @Test
    void maturityInPastShouldThrow() {
        TradeDto dto = new TradeDto("T1", 1, "CP", "B1", LocalDate.now().minusDays(1), null, false);
        assertThatThrownBy(() -> service.createOrReplace(dto))
                .isInstanceOf(MaturityInPastException.class);
    }
}
