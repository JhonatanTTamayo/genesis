package com.breaze.genesis.dto.users.responses;

import com.breaze.genesis.dto.auth.responses.PlanSummaryResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserListItemResponse {

    private Long id;
    private String fullName;
    private String email;
    private Boolean active;
    private Integer tokenBalance;
    private PlanSummaryResponse activePlan;
}