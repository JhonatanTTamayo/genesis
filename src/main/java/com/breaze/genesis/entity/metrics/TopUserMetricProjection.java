package com.breaze.genesis.entity.metrics;

public interface TopUserMetricProjection {

    Long getUserId();

    String getFullName();

    Long getTotalTokensConsumed();
}