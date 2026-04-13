package com.breaze.genesis.dto.operations.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BMIRequest {
    
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 3, fraction = 2, message = "Weight can only have up to 2 decimal places")
    private Double weightKg;
    
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 3, fraction = 2, message = "Height can only have up to 2 decimal places")
    private Double heightCm;
}
