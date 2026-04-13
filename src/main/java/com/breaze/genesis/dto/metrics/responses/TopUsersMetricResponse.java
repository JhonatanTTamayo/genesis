package com.breaze.genesis.dto.metrics.responses;

import java.util.List;

import com.breaze.genesis.dto.metrics.dto.TopUserMetricItem;

public class TopUsersMetricResponse {

    private List<TopUserMetricItem> items;

    public TopUsersMetricResponse() {
    }

    public List<TopUserMetricItem> getItems() {
        return items;
    }

    public void setItems(List<TopUserMetricItem> items) {
        this.items = items;
    }
}
