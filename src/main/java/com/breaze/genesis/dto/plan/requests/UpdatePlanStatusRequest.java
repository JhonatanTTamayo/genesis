package com.breaze.genesis.dto.plan.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanStatusRequest {
    @NotNull(message = "The active status cannot be null")
    private Boolean active;
}
