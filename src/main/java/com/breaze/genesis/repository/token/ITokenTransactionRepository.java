package com.breaze.genesis.repository.token;

import com.breaze.genesis.entity.metrics.DailyTokenConsumptionMetricProjection;
import com.breaze.genesis.entity.metrics.TopOperationMetricProjection;
import com.breaze.genesis.entity.metrics.TopUserMetricProjection;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ITokenTransactionRepository extends JpaRepository<TokenTransaction, Long> {

    Page<TokenTransaction> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT t FROM TokenTransaction t WHERE t.user.id = :userId AND t.remainingAmount > 0 AND (t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP) ORDER BY t.expiresAt ASC NULLS LAST, t.createdAt ASC")
    List<TokenTransaction> findValidPositiveTransactionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(t.remainingAmount), 0) FROM TokenTransaction t WHERE t.user.id = :userId AND t.remainingAmount > 0 AND (t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP)")
    Integer calculateValidBalance(@Param("userId") Long userId);

    @Query("""
            SELECT function('date', t.createdAt) AS metricDate,
                   SUM(CASE WHEN t.amount < 0 THEN -t.amount ELSE t.amount END) AS tokensConsumed
            FROM TokenTransaction t
            WHERE t.type = com.breaze.genesis.entity.tokens.TokenTransactionType.CONSUMPTION
            GROUP BY function('date', t.createdAt)
            ORDER BY function('date', t.createdAt) ASC
            """)
    List<DailyTokenConsumptionMetricProjection> findDailyTokenConsumptionMetrics();

    @Query("""
            SELECT CASE
                       WHEN t.description IS NULL OR trim(t.description) = '' THEN 'CONSUMPTION'
                       WHEN locate(' ', trim(t.description)) > 0 THEN substring(trim(t.description), 1, locate(' ', trim(t.description)) - 1)
                       ELSE 'CONSUMPTION'
                   END AS operationCode,
                   CASE
                       WHEN t.description IS NULL OR trim(t.description) = '' THEN 'Token Consumption'
                       WHEN locate(' ', trim(t.description)) > 0 THEN trim(substring(trim(t.description), locate(' ', trim(t.description)) + 1))
                       ELSE trim(t.description)
                   END AS operationName,
                   COUNT(t.id) AS executions
            FROM TokenTransaction t
            WHERE t.type = com.breaze.genesis.entity.tokens.TokenTransactionType.CONSUMPTION
            GROUP BY t.description
            ORDER BY COUNT(t.id) DESC, t.description ASC
            """)
    List<TopOperationMetricProjection> findTopOperationMetrics();

    @Query("""
            SELECT u.id AS userId,
                   u.fullName AS fullName,
                   SUM(CASE WHEN t.amount < 0 THEN -t.amount ELSE t.amount END) AS totalTokensConsumed
            FROM TokenTransaction t
            JOIN t.user u
            WHERE t.type = com.breaze.genesis.entity.tokens.TokenTransactionType.CONSUMPTION
            GROUP BY u.id, u.fullName
            ORDER BY SUM(CASE WHEN t.amount < 0 THEN -t.amount ELSE t.amount END) DESC, u.id ASC
            """)
    List<TopUserMetricProjection> findTopUserMetrics();
}
