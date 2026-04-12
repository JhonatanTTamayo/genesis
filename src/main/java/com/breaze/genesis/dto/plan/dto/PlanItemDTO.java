package com.breaze.genesis.dto.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanItemDTO {
    private Long id;
    private String name;
    private Integer tokenAmount;
    private Boolean active;
}
