package com.breaze.genesis.dto.plan.responses;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreatePlanResponse {

    private Long planId;
    private String planName;
    private String planDescription;
    private Integer tokenLimitPerCycle;
    private Integer billingCycleDurationSeconds;
    private LocalDateTime versionValidTo;
}