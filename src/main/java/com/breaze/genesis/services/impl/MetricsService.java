package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.metrics.dto.TokenConsumptionMetricItem;
import com.breaze.genesis.dto.metrics.dto.TopOperationMetricItem;
import com.breaze.genesis.dto.metrics.dto.TopUserMetricItem;
import com.breaze.genesis.dto.metrics.responses.TokenConsumptionMetricResponse;
import com.breaze.genesis.dto.metrics.responses.TopOperationsMetricResponse;
import com.breaze.genesis.dto.metrics.responses.TopUsersMetricResponse;
import com.breaze.genesis.entity.metrics.DailyTokenConsumptionMetricProjection;
import com.breaze.genesis.entity.metrics.TopOperationMetricProjection;
import com.breaze.genesis.entity.metrics.TopUserMetricProjection;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.services.IMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsService implements IMetricsService {

    private final ITokenTransactionRepository tokenTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public TokenConsumptionMetricResponse getTokenConsumptionMetrics() {
        List<TokenConsumptionMetricItem> items = tokenTransactionRepository.findDailyTokenConsumptionMetrics()
                .stream()
                .map(this::toTokenConsumptionItem)
                .toList();

        TokenConsumptionMetricResponse response = new TokenConsumptionMetricResponse();
        response.setItems(items);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TopOperationsMetricResponse getTopOperationsMetrics() {
        List<TopOperationMetricItem> items = tokenTransactionRepository.findTopOperationMetrics()
                .stream()
                .map(this::toTopOperationItem)
                .toList();

        TopOperationsMetricResponse response = new TopOperationsMetricResponse();
        response.setItems(items);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TopUsersMetricResponse getTopUsersMetrics() {
        List<TopUserMetricItem> items = tokenTransactionRepository.findTopUserMetrics()
                .stream()
                .map(this::toTopUserItem)
                .toList();

        TopUsersMetricResponse response = new TopUsersMetricResponse();
        response.setItems(items);
        return response;
    }

    private TokenConsumptionMetricItem toTokenConsumptionItem(DailyTokenConsumptionMetricProjection projection) {
        TokenConsumptionMetricItem item = new TokenConsumptionMetricItem();
        item.setDate(projection.getMetricDate());
        item.setTokensConsumed(toInteger(projection.getTokensConsumed()));
        return item;
    }

    private TopOperationMetricItem toTopOperationItem(TopOperationMetricProjection projection) {
        TopOperationMetricItem item = new TopOperationMetricItem();
        item.setOperationCode(projection.getOperationCode());
        item.setOperationName(projection.getOperationName());
        item.setExecutions(toInteger(projection.getExecutions()));
        return item;
    }

    private TopUserMetricItem toTopUserItem(TopUserMetricProjection projection) {
        TopUserMetricItem item = new TopUserMetricItem();
        item.setUserId(projection.getUserId());
        item.setFullName(projection.getFullName());
        item.setTotalTokensConsumed(toInteger(projection.getTotalTokensConsumed()));
        return item;
    }

    private Integer toInteger(Long value) {
        if (value == null) {
            return 0;
        }
        return Math.toIntExact(value);
    }
}
