package com.breaze.genesis.services;

import com.breaze.genesis.dto.plan.requests.ListPlansRequest;
import com.breaze.genesis.dto.plan.responses.ListPlansResponse;

public interface IPlanService {
    ListPlansResponse getPlans(ListPlansRequest request);
}
