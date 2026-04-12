package com.breaze.genesis.dto.response;

import java.util.List;

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
