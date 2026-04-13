package com.breaze.genesis.repository.token;

import com.breaze.genesis.entity.tokens.TokenTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITokenTransactionRepository extends JpaRepository<TokenTransaction, Long> {

    Page<TokenTransaction> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT t FROM TokenTransaction t WHERE t.user.id = :userId AND t.remainingAmount > 0 AND (t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP) ORDER BY t.expiresAt ASC NULLS LAST, t.createdAt ASC")
    List<TokenTransaction> findValidPositiveTransactionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(t.remainingAmount), 0) FROM TokenTransaction t WHERE t.user.id = :userId AND t.remainingAmount > 0 AND (t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP)")
    Integer calculateValidBalance(@Param("userId") Long userId);
}
