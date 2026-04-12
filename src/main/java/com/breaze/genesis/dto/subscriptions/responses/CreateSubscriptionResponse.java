package com.breaze.genesis.dto.subscriptions.responses;

import com.breaze.genesis.dto.subscriptions.dto.SubscriptionPlanDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubscriptionResponse {
    private Long userId;
    private SubscriptionPlanDTO plan;
    private Integer newTokenBalance;
    private LocalDateTime startDate;
}
