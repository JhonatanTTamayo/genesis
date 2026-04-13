package com.breaze.genesis.dto.plan.requests;

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
/**
 * DTO de entrada para actualizar un plan y crear una nueva version de vigencia.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class UpdatePlanRequest {

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

    @Future(message = "versionValidTo must be a future date-time")
    private LocalDateTime versionValidTo;
}
