package com.breaze.genesis.dto.wallet.dto;

import com.breaze.genesis.entity.tokens.TokenTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionItemDTO {
    private Long id;
    private Integer amount;
    private TokenTransactionType type;
    private Long referenceId;
    private LocalDateTime createdAt;
}
