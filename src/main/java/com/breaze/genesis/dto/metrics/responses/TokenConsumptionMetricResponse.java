package com.breaze.genesis.dto.metrics.responses;

import java.util.List;

import com.breaze.genesis.dto.metrics.dto.TokenConsumptionMetricItem;

public class TokenConsumptionMetricResponse {

    private List<TokenConsumptionMetricItem> items;

    public TokenConsumptionMetricResponse() {
    }

    public List<TokenConsumptionMetricItem> getItems() {
        return items;
    }

    public void setItems(List<TokenConsumptionMetricItem> items) {
        this.items = items;
    }
}
