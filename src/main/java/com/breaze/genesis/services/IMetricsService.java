package com.breaze.genesis.services;

import com.breaze.genesis.dto.metrics.responses.TokenConsumptionMetricResponse;
import com.breaze.genesis.dto.metrics.responses.TopOperationsMetricResponse;
import com.breaze.genesis.dto.metrics.responses.TopUsersMetricResponse;

public interface IMetricsService {

    TokenConsumptionMetricResponse getTokenConsumptionMetrics();

    TopOperationsMetricResponse getTopOperationsMetrics();

    TopUsersMetricResponse getTopUsersMetrics();
}