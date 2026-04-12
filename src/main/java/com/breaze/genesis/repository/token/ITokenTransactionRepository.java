package com.breaze.genesis.repository.token;

import com.breaze.genesis.entity.tokens.TokenTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITokenTransactionRepository extends JpaRepository<TokenTransaction, Long> {
    Optional<TokenTransaction> findByIdempotencyKey(String idempotencyKey);

    Page<TokenTransaction> findByUserId(Long userId, Pageable pageable);
}
