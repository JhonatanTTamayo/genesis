package com.breaze.genesis.dto.response;

public class TopUserMetricItem {

    private Long userId;
    private String fullName;
    private Integer totalTokensConsumed;

    public TopUserMetricItem() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getTotalTokensConsumed() {
        return totalTokensConsumed;
    }

    public void setTotalTokensConsumed(Integer totalTokensConsumed) {
        this.totalTokensConsumed = totalTokensConsumed;
    }
}
