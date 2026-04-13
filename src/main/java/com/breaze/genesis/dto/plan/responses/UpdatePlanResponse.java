package com.breaze.genesis.dto.plan.responses;

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
 * DTO de salida con el resultado de actualizacion y versionado de un plan.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class UpdatePlanResponse {
    private Long planId;
    private String planName;
    private String planDescription;
    private Integer tokenLimitPerCycle;
    private Integer billingCycleDurationSeconds;
    private LocalDateTime versionValidFrom;
    private LocalDateTime versionValidTo;
    private Boolean currentlyAvailable;
}
