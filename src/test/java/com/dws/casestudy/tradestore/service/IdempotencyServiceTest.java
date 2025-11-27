package com.dws.casestudy.tradestore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.dws.casestudy.tradestore.entity.IdempotencyKey;
import com.dws.casestudy.tradestore.repository.IdempotencyRepository;

public class IdempotencyServiceTest {

    private IdempotencyRepository repo;
    private IdempotencyService service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(IdempotencyRepository.class);
        service = new IdempotencyService(repo);
    }

    @Test
    void saveAndGetShouldWork() {
        Mockito.when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.save("key1", 200, "{}");

        IdempotencyKey key = new IdempotencyKey();
        key.setId("key1");
        key.setStatusCode(200);
        key.setResponseBody("{}");

        Mockito.when(repo.findById("key1")).thenReturn(Optional.of(key));

        var stored = service.get("key1").get();
        assertThat(stored.getStatusCode()).isEqualTo(200);
    }
}
