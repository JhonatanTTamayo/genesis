package com.breaze.genesis.dto.operations.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditExecutionResponse {
    private Integer monthlyPayment;
    private Integer totalPayment;
    private Integer totalInterest;

}
