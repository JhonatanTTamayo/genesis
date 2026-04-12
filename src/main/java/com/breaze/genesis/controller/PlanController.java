package com.breaze.genesis.controller;

import com.breaze.genesis.dto.plan.requests.ListPlansRequest;
import com.breaze.genesis.dto.plan.responses.ListPlansResponse;
import com.breaze.genesis.services.IPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {

    private final IPlanService planService;

    @GetMapping
    public ResponseEntity<ListPlansResponse> getPlans(@Valid @ModelAttribute ListPlansRequest request) {
        return ResponseEntity.ok(planService.getPlans(request));
    }
}
