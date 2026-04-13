package com.breaze.genesis.dto.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de salida para representar un plan en listados.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class PlanItemDTO {
    private Long planId;
    private String planName;
    private String planDescription;
    private Integer tokenLimitPerCycle;
    private Integer billingCycleDurationSeconds;
}
