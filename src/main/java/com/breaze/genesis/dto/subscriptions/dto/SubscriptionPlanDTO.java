package com.breaze.genesis.dto.subscriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private Integer tokenAmount;
}
