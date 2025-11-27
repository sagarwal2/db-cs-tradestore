package com.dws.casestudy.tradestore.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.dws.casestudy.tradestore.service.TradeService;

public class TradeExpirySchedulerTest {

    @Test
    void schedulerShouldInvokeExpiry() {
        TradeService svc = Mockito.mock(TradeService.class);
        TradeExpiryScheduler scheduler = new TradeExpiryScheduler(svc);

        scheduler.markExpiredDaily();

        verify(svc, times(1)).markExpired();
    }
}
