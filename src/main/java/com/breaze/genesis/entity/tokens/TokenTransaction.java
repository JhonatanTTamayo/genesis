package com.breaze.genesis.entity.tokens;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false, length = 50)
    private TokenTransactionReferenceType referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "remaining_amount")
    private Integer remainingAmount;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        validateTransaction();
        normalizeAmountByReferenceSemantics();
        initializeRemainingAmount();
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * Valida que el monto y la referencia de transaccion sean correctos.
     */
    private void validateTransaction() {
        if (this.amount == null || this.amount == 0) {
            throw new IllegalStateException("Transaction amount must be different from 0");
        }
        if (this.referenceType == null) {
            throw new IllegalStateException("Transaction reference type is required");
        }
    }

    /**
     * Normaliza el monto (positivo o negativo) segun el tipo de referencia.
     */
    private void normalizeAmountByReferenceSemantics() {
        if (this.referenceType.isTokenAdditionMovement() && this.amount < 0) {
            this.amount = Math.abs(this.amount);
        }
        if (this.referenceType.isTokenSubtractionMovement() && this.amount > 0) {
            this.amount = -Math.abs(this.amount);
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
