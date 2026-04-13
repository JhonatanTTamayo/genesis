package com.breaze.genesis.dto.subscriptions.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCreateRequest {
    @NotNull(message = "Subscription plan ID cannot be null")
    @Positive(message = "Subscription plan ID must be a positive number")
    private Long planId;
}
