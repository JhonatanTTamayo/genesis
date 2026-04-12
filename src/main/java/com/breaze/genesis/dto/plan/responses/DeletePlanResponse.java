package com.breaze.genesis.dto.plan.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de salida para la operacion de eliminacion de un plan.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class DeletePlanResponse {
    private Long planId;
    private Boolean deleted;
    private String message;
}
