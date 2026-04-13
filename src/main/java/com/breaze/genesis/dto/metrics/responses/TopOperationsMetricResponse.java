package com.breaze.genesis.dto.metrics.responses;

import java.util.List;

import com.breaze.genesis.dto.metrics.dto.TopOperationMetricItem;

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
