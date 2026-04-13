package com.breaze.genesis.dto.operations.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditExecutionRequest {
    
    @NotNull
    @DecimalMin("0.01")
    private Double amount;
    
    @NotNull
    @Min(1)
    private Integer installments;
    
    @NotNull
    @DecimalMin("0.01")
    private Double monthlyRate;
}
