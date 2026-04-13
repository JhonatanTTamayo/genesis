package com.breaze.genesis.dto.operations.responses;

import com.breaze.genesis.dto.operations.dto.ConversionDirection;
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
    private Double convertedAmount;
    private ConversionDirection conversionDirection;
    private Double appliedRate;
    private LocalDateTime rateLastUpdatedAt;
}
