package com.breaze.genesis.dto.subscriptions.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListSubscriptionHistoryResponse {
    private Long id;
    private String planName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
}
