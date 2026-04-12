package com.breaze.genesis.services;

import com.breaze.genesis.dto.subscriptions.requests.SubscriptionCreateRequest;
import com.breaze.genesis.dto.subscriptions.requests.MyActiveSubscriptionRequest;
import com.breaze.genesis.dto.subscriptions.requests.SubscriptionHistoryQueryRequest;
import com.breaze.genesis.dto.subscriptions.responses.CreateSubscriptionResponse;
import com.breaze.genesis.dto.subscriptions.responses.ListSubscriptionHistoryResponse;
import com.breaze.genesis.dto.subscriptions.responses.MyActiveSubscriptionResponse;

import java.util.List;

public interface ISubscriptionService {
    List<ListSubscriptionHistoryResponse> getHistory(String authenticatedEmail , SubscriptionHistoryQueryRequest request);

    CreateSubscriptionResponse createSubscription(String authenticatedEmail, SubscriptionCreateRequest request);

    MyActiveSubscriptionResponse getMyActiveSubscription(MyActiveSubscriptionRequest request);
}
