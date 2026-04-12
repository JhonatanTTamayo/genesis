package com.breaze.genesis.repository;

import com.breaze.genesis.entity.Transaction;
import com.breaze.genesis.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    @Query(value = """
            SELECT DATE(t.executed_at) AS metric_date, SUM(t.total_tokens_consumed) AS tokens_consumed
            FROM transactions t
            GROUP BY DATE(t.executed_at)
            ORDER BY DATE(t.executed_at) ASC
            """, nativeQuery = true)
    List<Object[]> findDailyTokenConsumptionMetrics();

    @Query(value = """
            SELECT t.operation_code, t.operation_name, COUNT(*) AS executions
            FROM transactions t
            GROUP BY t.operation_code, t.operation_name
            ORDER BY COUNT(*) DESC, t.operation_code ASC
            """, nativeQuery = true)
    List<Object[]> findTopOperationMetrics();

    @Query(value = """
            SELECT u.id, u.full_name, SUM(t.total_tokens_consumed) AS total_tokens_consumed
            FROM transactions t
            INNER JOIN users u ON u.id = t.user_id
            GROUP BY u.id, u.full_name
            ORDER BY SUM(t.total_tokens_consumed) DESC, u.id ASC
            """, nativeQuery = true)
    List<Object[]> findTopUserMetrics();
}
