package com.breaze.genesis.controller;

import com.breaze.genesis.dto.response.TokenConsumptionMetricResponse;
import com.breaze.genesis.dto.response.TopOperationsMetricResponse;
import com.breaze.genesis.dto.response.TopUsersMetricResponse;
import com.breaze.genesis.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/token-consumption")
    public TokenConsumptionMetricResponse getTokenConsumptionMetrics() {
        return metricsService.getTokenConsumptionMetrics();
    }

    @GetMapping("/top-operations")
    public TopOperationsMetricResponse getTopOperationsMetrics() {
        return metricsService.getTopOperationsMetrics();
    }

    @GetMapping("/top-users")
    public TopUsersMetricResponse getTopUsersMetrics() {
        return metricsService.getTopUsersMetrics();
    }
}
