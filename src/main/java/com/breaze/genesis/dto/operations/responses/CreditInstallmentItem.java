package com.breaze.genesis.dto.operations.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditInstallmentItem {
    private Integer month;
    private BigDecimal interest;
    private BigDecimal principalPaid;
    private BigDecimal remainingBalance;
}
