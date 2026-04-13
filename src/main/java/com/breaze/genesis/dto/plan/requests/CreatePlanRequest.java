package com.breaze.genesis.dto.plan.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanRequest {

    @NotBlank(message = "planName is required")
    private String planName;

    @NotBlank(message = "planDescription is required")
    private String planDescription;

    @NotNull(message = "tokenLimitPerCycle is required")
    @Positive(message = "tokenLimitPerCycle must be greater than 0")
    private Integer tokenLimitPerCycle;

    @NotNull(message = "billingCycleDurationSeconds is required")
    @Positive(message = "billingCycleDurationSeconds must be greater than 0")
    private Integer billingCycleDurationSeconds;

    @JsonAlias("valid_to")
    @Future(message = "versionValidTo must be a future date-time")
    private LocalDateTime versionValidTo;
}