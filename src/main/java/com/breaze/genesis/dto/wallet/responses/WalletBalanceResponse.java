package com.breaze.genesis.dto.wallet.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponse {
    private Long userId;
    private String email;
    private Integer currentBalance;
    private LocalDateTime updatedAt;
}
