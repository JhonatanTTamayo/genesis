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
public class TokenRechargeResponse {
    private Long userId;
    private Integer newBalance;
    private Integer rechargedAmount;
    private LocalDateTime transactionDate;
}
