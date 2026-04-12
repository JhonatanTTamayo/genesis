package com.breaze.genesis.dto.operations.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditExecutionRequest {
    private Integer price;
    private Integer quantity;
    private Integer monthlyInterestRate;
}
