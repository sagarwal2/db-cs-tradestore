package com.dws.casestudy.tradestore.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TradeId implements Serializable {
    private static final long serialVersionUID = -2546224470136479623L;
	private String tradeId;
    private Integer version;
}
