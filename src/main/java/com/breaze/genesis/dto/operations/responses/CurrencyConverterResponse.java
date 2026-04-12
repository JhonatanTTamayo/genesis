package com.breaze.genesis.dto.operations.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConverterResponse {
    private String amountConverted;
    private String conversionDirection;
    private Integer taxApplied;
    private LocalDateTime taxUpdateDate;
}
