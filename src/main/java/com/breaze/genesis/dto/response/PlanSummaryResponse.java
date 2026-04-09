package com.breaze.genesis.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanSummaryResponse {

    private Long id;
    private String name;
    private Integer tokenAmount;
}