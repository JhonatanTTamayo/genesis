package com.breaze.genesis.dto.operations.responses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ExchangeRateResponse {
    private Long id;
    private Double copPerUsd;
    private LocalDateTime updatedAt;
}
