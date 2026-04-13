package com.breaze.genesis.entity.metrics;

import java.time.LocalDate;

public interface DailyTokenConsumptionMetricProjection {

    LocalDate getMetricDate();

    Long getTokensConsumed();
}