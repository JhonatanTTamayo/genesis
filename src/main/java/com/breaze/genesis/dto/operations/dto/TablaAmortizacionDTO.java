package com.breaze.genesis.dto.operations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TablaAmortizacionDTO {
    private Integer paymentNumber;
    private Integer amortizedCapital;
    private Integer payedInterest;
    private Integer leftCapital;
    private OriginCurrency currency;
}
