package com.breaze.genesis.dto.response;

import java.util.List;

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
