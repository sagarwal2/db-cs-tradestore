package com.dws.casestudy.tradestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dws.casestudy.tradestore.entity.IdempotencyKey;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, String> {
}