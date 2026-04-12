package com.breaze.genesis.service;

import com.breaze.genesis.dto.response.TokenConsumptionMetricItem;
import com.breaze.genesis.dto.response.TokenConsumptionMetricResponse;
import com.breaze.genesis.dto.response.TopOperationMetricItem;
import com.breaze.genesis.dto.response.TopOperationsMetricResponse;
import com.breaze.genesis.dto.response.TopUserMetricItem;
import com.breaze.genesis.dto.response.TopUsersMetricResponse;
import com.breaze.genesis.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricsService {

    private final TransactionRepository transactionRepository;

    public MetricsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public TokenConsumptionMetricResponse getTokenConsumptionMetrics() {
        List<Object[]> rows = transactionRepository.findDailyTokenConsumptionMetrics();
        List<TokenConsumptionMetricItem> items = new ArrayList<>();
        int i;

        for (i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            TokenConsumptionMetricItem item = new TokenConsumptionMetricItem();
            item.setDate(extractLocalDate(row[0]));
            item.setTokensConsumed(extractInteger(row[1]));
            items.add(item);
        }

        TokenConsumptionMetricResponse response = new TokenConsumptionMetricResponse();
        response.setItems(items);
        return response;
    }

    @Transactional(readOnly = true)
    public TopOperationsMetricResponse getTopOperationsMetrics() {
        List<Object[]> rows = transactionRepository.findTopOperationMetrics();
        List<TopOperationMetricItem> items = new ArrayList<>();
        int i;

        for (i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            TopOperationMetricItem item = new TopOperationMetricItem();
            item.setOperationCode(extractString(row[0]));
            item.setOperationName(extractString(row[1]));
            item.setExecutions(extractInteger(row[2]));
            items.add(item);
        }

        TopOperationsMetricResponse response = new TopOperationsMetricResponse();
        response.setItems(items);
        return response;
    }

    @Transactional(readOnly = true)
    public TopUsersMetricResponse getTopUsersMetrics() {
        List<Object[]> rows = transactionRepository.findTopUserMetrics();
        List<TopUserMetricItem> items = new ArrayList<>();
        int i;

        for (i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            TopUserMetricItem item = new TopUserMetricItem();
            item.setUserId(extractLong(row[0]));
            item.setFullName(extractString(row[1]));
            item.setTotalTokensConsumed(extractInteger(row[2]));
            items.add(item);
        }

        TopUsersMetricResponse response = new TopUsersMetricResponse();
        response.setItems(items);
        return response;
    }

    private LocalDate extractLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof Date) {
            return ((Date) value).toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }

    private Integer extractInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private Long extractLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    private String extractString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
