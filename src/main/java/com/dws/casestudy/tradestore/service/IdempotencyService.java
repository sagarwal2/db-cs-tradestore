package com.dws.casestudy.tradestore.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dws.casestudy.tradestore.entity.IdempotencyKey;
import com.dws.casestudy.tradestore.repository.IdempotencyRepository;

@Service
public class IdempotencyService {

    private final IdempotencyRepository repo;

    public IdempotencyService(IdempotencyRepository repo) {
        this.repo = repo;
    }

    public Optional<IdempotencyKey> get(String key) {
        return repo.findById(key);
    }

    public void save(String key, int status, String response) {
        IdempotencyKey e = new IdempotencyKey();
        e.setId(key);
        e.setStatusCode(status);
        e.setResponseBody(response);
        repo.save(e);
    }
}