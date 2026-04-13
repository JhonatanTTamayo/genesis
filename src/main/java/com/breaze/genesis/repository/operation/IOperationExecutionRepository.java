package com.breaze.genesis.repository.operation;

import com.breaze.genesis.entity.metrics.DailyTokenConsumptionMetricProjection;
import com.breaze.genesis.entity.metrics.TopOperationMetricProjection;
import com.breaze.genesis.entity.metrics.TopUserMetricProjection;
import com.breaze.genesis.entity.transactions.OperationExecution;
import com.breaze.genesis.entity.transactions.OperationExecutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IOperationExecutionRepository extends JpaRepository<OperationExecution, Long> {

    Page<OperationExecution> findByUserId(Long userId, Pageable pageable);

    Optional<OperationExecution> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT function('date', e.executedAt) AS metricDate,
                   SUM(e.totalTokensConsumed) AS tokensConsumed
            FROM OperationExecution e
            WHERE e.status = :status
            GROUP BY function('date', e.executedAt)
            ORDER BY function('date', e.executedAt) ASC
            """)
    List<DailyTokenConsumptionMetricProjection> findDailyTokenConsumptionMetrics(
            @Param("status") OperationExecutionStatus status
    );

    @Query("""
            SELECT e.operationCode AS operationCode,
                   e.operationName AS operationName,
                   COUNT(e.id) AS executions
            FROM OperationExecution e
            WHERE e.status = :status
            GROUP BY e.operationCode, e.operationName
            ORDER BY COUNT(e.id) DESC, e.operationCode ASC
            """)
    List<TopOperationMetricProjection> findTopOperationMetrics(@Param("status") OperationExecutionStatus status);

    @Query("""
            SELECT u.id AS userId,
                   u.fullName AS fullName,
                   SUM(e.totalTokensConsumed) AS totalTokensConsumed
            FROM OperationExecution e
            JOIN e.user u
            WHERE e.status = :status
            GROUP BY u.id, u.fullName
            ORDER BY SUM(e.totalTokensConsumed) DESC, u.id ASC
            """)
    List<TopUserMetricProjection> findTopUserMetrics(@Param("status") OperationExecutionStatus status);
}
