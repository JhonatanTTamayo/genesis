package com.breaze.genesis.entity.metrics;

public interface TopOperationMetricProjection {

    String getOperationCode();

    String getOperationName();

    Long getExecutions();
}