package com.dws.casestudy.tradestore.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dws.casestudy.tradestore.dto.TradeDto;
import com.dws.casestudy.tradestore.entity.Trade;
import com.dws.casestudy.tradestore.exception.LowerVersionException;
import com.dws.casestudy.tradestore.exception.MaturityInPastException;
import com.dws.casestudy.tradestore.repository.TradeRepository;

import jakarta.transaction.Transactional;

@Service
public class TradeService {

    private final TradeRepository repo;

    public TradeService(TradeRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ServiceResult createOrReplace(TradeDto dto) {
        LocalDate today = LocalDate.now();
    	
        // Reject any trade whose Maturity Date is earlier than today's date at creation/update time.
        if (dto.getMaturityDate().isBefore(today)) {
            throw new MaturityInPastException("maturityDate " + dto.getMaturityDate() + " is before today");
        }

        Optional<Trade> latestOpt = repo.findTopByTradeIdForUpdate(dto.getTradeId());

        if (latestOpt.isPresent()) {
            Trade latest = latestOpt.get();
            int latestVersion = latest.getId().getVersion();
            if (dto.getVersion() < latestVersion) {
                throw new LowerVersionException("incoming version " + dto.getVersion() + " is lower than stored latest version " + latestVersion);
            }
            if (dto.getVersion().equals(latestVersion)) {
                // replace
                latest.setBookId(dto.getBookId());
                latest.setCounterPartyId(dto.getCounterPartyId());
                latest.setMaturityDate(dto.getMaturityDate());
                latest.setCreatedDate(LocalDateTime.now());
                latest.setExpired(dto.isExpired());
                repo.save(latest);
                return new ServiceResult(false, TradeDto.fromEntity(latest));
            } else {
                // new version > latest
                Trade t = dto.toEntity();
                t.setCreatedDate(LocalDateTime.now());
                repo.save(t);
                return new ServiceResult(true, TradeDto.fromEntity(t));
            }
        } else {
            Trade t = dto.toEntity();
            t.setCreatedDate(LocalDateTime.now());
            repo.save(t);
            return new ServiceResult(true, TradeDto.fromEntity(t));
        }
    }

    public Optional<TradeDto> getLatest(String tradeId) {
        return repo.findTopByTradeIdForUpdate(tradeId).map(TradeDto::fromEntity);
    }

    public Optional<TradeDto> getByVersion(String tradeId, Integer version) {
        return repo.findByIdTradeIdAndIdVersion(tradeId, version).map(TradeDto::fromEntity);
    }

    @Transactional
    public int markExpired() {
        return repo.markExpired(LocalDate.now());
    }

    public Page<TradeDto> searchTrades(String counterPartyId, Boolean expired, Pageable pageable) {
        return repo.search(counterPartyId, expired, pageable)
                   .map(TradeDto::fromEntity);
    }

    public static class ServiceResult {
        private final boolean created;
        private final TradeDto dto;

        public ServiceResult(boolean created, TradeDto dto) {
            this.created = created;
            this.dto = dto;
        }

        public boolean isCreated() { return created; }
        public TradeDto getDto() { return dto; }
    }

}