package com.breaze.genesis.entity.tokens;

import com.breaze.genesis.entity.User;
import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.transactions.OperationExecution;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_execution_id")
    private OperationExecution operationExecution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "remaining_amount")
    private Integer remainingAmount;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenTransactionType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        validateTransaction();
        normalizeAmountByOperationSemantics();
        initializeRemainingAmount();

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Valida que el monto y el tipo de transaccion sean correctos.
     */
    private void validateTransaction() {
        if (this.amount == null || this.amount == 0) {
            throw new IllegalStateException("Transaction amount must be different from 0");
        }
        if (this.type == null) {
            throw new IllegalStateException("Transaction type is required");
        }
    }

    /**
     * Normaliza el monto (positivo o negativo) segun el tipo de transaccion.
     */
    private void normalizeAmountByOperationSemantics() {
        // ADD (top-up manual admin) y SUBSCRIPTION aportan (+)
        if ((this.type == TokenTransactionType.ADD || this.type == TokenTransactionType.SUBSCRIPTION) && this.amount < 0) {
            this.amount = Math.abs(this.amount);
        }
        // CONSUMPTION sustrae (-)
        if ((this.type == TokenTransactionType.CONSUMPTION) && this.amount > 0) {
            this.amount = -this.amount;
        }
    }

    /**
     * Inicializa el saldo restante solo para saldos positivos.
     */
    private void initializeRemainingAmount() {
        if (this.amount > 0) {
            if (this.remainingAmount == null) {
                this.remainingAmount = this.amount;
            }
        } else {
            this.remainingAmount = null;
        }
    }
}
