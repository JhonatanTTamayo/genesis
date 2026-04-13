package com.breaze.genesis.dto.operations.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExchangeRateRequest {

    @NotNull(message = "The exchange rate (copPerUsd) cannot be null")
    @DecimalMin(value = "0.01", message = "The exchange rate must be strictly positive")
    private Double copPerUsd;

}
