package com.dws.casestudy.tradestore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
public class Trade {
    @EmbeddedId
    private TradeId id;

    private String counterPartyId;
    private String bookId;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean expired = false;

    public static Trade of(String tradeId, Integer version) {
        Trade t = new Trade();
        t.id = new TradeId(tradeId, version);
        return t;
    }
}
