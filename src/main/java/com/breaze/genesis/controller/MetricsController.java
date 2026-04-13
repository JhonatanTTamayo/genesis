package com.breaze.genesis.controller;

import com.breaze.genesis.dto.metrics.responses.TokenConsumptionMetricResponse;
import com.breaze.genesis.dto.metrics.responses.TopOperationsMetricResponse;
import com.breaze.genesis.dto.metrics.responses.TopUsersMetricResponse;
import com.breaze.genesis.services.IMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final IMetricsService metricsService;

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
