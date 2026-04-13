package com.breaze.genesis.dto.auth.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private Boolean active;
    private Integer tokenBalance;
    private PlanSummaryResponse activePlan;
}