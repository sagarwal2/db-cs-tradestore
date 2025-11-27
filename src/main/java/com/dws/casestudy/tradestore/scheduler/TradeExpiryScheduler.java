package com.dws.casestudy.tradestore.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dws.casestudy.tradestore.service.TradeService;

@Component
@EnableScheduling
public class TradeExpiryScheduler {
	private static final Logger log = LoggerFactory.getLogger(TradeExpiryScheduler.class);
	private final TradeService tradeService;

	public TradeExpiryScheduler(TradeService tradeService) {
		this.tradeService = tradeService;
	}

	// Run daily at 00:05 server time
	@Scheduled(cron = "0 5 0 * * ?")
	public void markExpiredDaily() {
		int updated = tradeService.markExpired();
		log.info("Marked {} trades expired", updated);
	}

}
