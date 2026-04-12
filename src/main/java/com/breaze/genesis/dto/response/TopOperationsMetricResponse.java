package com.breaze.genesis.dto.response;

import java.util.List;

public class TopOperationsMetricResponse {

    private List<TopOperationMetricItem> items;

    public TopOperationsMetricResponse() {
    }

    public List<TopOperationMetricItem> getItems() {
        return items;
    }

    public void setItems(List<TopOperationMetricItem> items) {
        this.items = items;
    }
}
