package com.breaze.genesis.dto.tokentransactions.dto;

import com.breaze.genesis.entity.tokens.TokenTransactionReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransactionItemDTO {
    private Long id;
    private Integer amount;
    private TokenTransactionReferenceType referenceType;
    private Long referenceId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}