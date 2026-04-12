package com.breaze.genesis.dto.response;

import java.time.LocalDate;

public class TokenConsumptionMetricItem {

    private LocalDate date;
    private Integer tokensConsumed;

    public TokenConsumptionMetricItem() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTokensConsumed() {
        return tokensConsumed;
    }

    public void setTokensConsumed(Integer tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }
}
