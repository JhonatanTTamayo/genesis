package com.breaze.genesis.entity.transactions;

import com.breaze.genesis.entity.Operation;
import com.breaze.genesis.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_executions")
public class OperationExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_catalog_id")
    private Operation operation;

    @Column(name = "base_cost", nullable = false)
    private Integer baseCost;

    @Column(name = "total_tokens_consumed", nullable = false)
    private Integer totalTokensConsumed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperationExecutionStatus status;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @PrePersist
    public void prePersist() {
        if (this.executedAt == null) {
            this.executedAt = LocalDateTime.now();
        }
        if (this.baseCost == null) {
            this.baseCost = 0;
        }
        if (this.totalTokensConsumed == null) {
            this.totalTokensConsumed = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Operation getOperationCatalog() {
        return operation;
    }

    public void setOperationCatalog(Operation operation) {
        this.operation = operation;
    }

    public Integer getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Integer baseCost) {
        this.baseCost = baseCost;
    }

    public Integer getTotalTokensConsumed() {
        return totalTokensConsumed;
    }

    public void setTotalTokensConsumed(Integer totalTokensConsumed) {
        this.totalTokensConsumed = totalTokensConsumed;
    }

    public OperationExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(OperationExecutionStatus status) {
        this.status = status;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}
