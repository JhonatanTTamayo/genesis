package com.breaze.genesis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "operation_code", nullable = false, length = 20)
    private String operationCode;

    @Column(name = "operation_name", nullable = false, length = 150)
    private String operationName;

    @Lob
    @Column(name = "input_json", columnDefinition = "TEXT")
    private String inputJson;

    @Lob
    @Column(name = "output_json", columnDefinition = "TEXT")
    private String outputJson;

    @Column(name = "input_tokens", nullable = false)
    private Integer inputTokens;

    @Column(name = "output_tokens", nullable = false)
    private Integer outputTokens;

    @Column(name = "base_cost", nullable = false)
    private Integer baseCost;

    @Column(name = "total_tokens_consumed", nullable = false)
    private Integer totalTokensConsumed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @PrePersist
    public void prePersist() {
        if (this.executedAt == null) {
            this.executedAt = LocalDateTime.now();
        }
    }
}