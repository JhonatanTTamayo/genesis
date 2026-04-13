package com.breaze.genesis.dto.plan.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanStatusResponse {
    private Long planId;
    private Boolean active;
    private String message;
}
