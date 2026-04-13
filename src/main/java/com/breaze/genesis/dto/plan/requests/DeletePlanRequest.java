package com.breaze.genesis.dto.plan.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de entrada para eliminar un plan por identificador.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class DeletePlanRequest {

    @NotNull(message = "planId is required")
    @Positive(message = "planId must be a positive number")
    private Long planId;
}
