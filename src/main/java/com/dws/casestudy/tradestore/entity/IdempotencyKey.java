package com.dws.casestudy.tradestore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@NoArgsConstructor
public class IdempotencyKey {
	@Id
	private String id;

	private int statusCode;
	@Lob
	private String responseBody;
	private LocalDateTime createdAt = LocalDateTime.now();
}
