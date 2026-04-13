package com.breaze.genesis.dto.operations.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditExecutionResponse {
    private Double monthlyPayment;
    private Double totalPaid;
    private Double totalInterest;
    private List<AmortizationItem> amortizationSchedule;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AmortizationItem {
        private Integer month;
        private Double interestPaid;
        private Double principalPaid;
        private Double remainingBalance;
    }
}
