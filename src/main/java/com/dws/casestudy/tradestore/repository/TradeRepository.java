package com.dws.casestudy.tradestore.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dws.casestudy.tradestore.entity.Trade;
import com.dws.casestudy.tradestore.entity.TradeId;

import jakarta.persistence.LockModeType;

@Repository
public interface TradeRepository extends JpaRepository<Trade, TradeId> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from Trade t where t.id.tradeId = :tradeId order by t.id.version desc")
	Optional<Trade> findTopByTradeIdForUpdate(@Param("tradeId") String tradeId);

	Optional<Trade> findByIdTradeIdAndIdVersion(String tradeId, Integer version);

	@Modifying
	@Query("update Trade t set t.expired = true where t.maturityDate < :today and t.expired = false")
	int markExpired(@Param("today") LocalDate today);

	@Query("""
			SELECT t FROM Trade t
			WHERE (:counterPartyId IS NULL OR t.counterPartyId = :counterPartyId)
			  AND (:expired IS NULL OR t.expired = :expired)
			""")
	Page<Trade> search(@Param("counterPartyId") String counterPartyId, @Param("expired") Boolean expired,
			Pageable pageable);
}
