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
/**
 * DTO de salida con saldo actual del wallet.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class WalletBalanceResponse {
    private Long userId;
    private String email;
    private Integer currentBalance;
    private LocalDateTime updatedAt;
}
