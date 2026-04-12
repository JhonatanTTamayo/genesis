package com.breaze.genesis.controller;

import com.breaze.genesis.dto.subscriptions.requests.SubscriptionCreateRequest;
import com.breaze.genesis.dto.subscriptions.requests.SubscriptionHistoryQueryRequest;
import com.breaze.genesis.dto.subscriptions.requests.MyActiveSubscriptionRequest;
import com.breaze.genesis.dto.subscriptions.responses.CreateSubscriptionResponse;
import com.breaze.genesis.dto.subscriptions.responses.ListSubscriptionHistoryResponse;
import com.breaze.genesis.dto.subscriptions.responses.MyActiveSubscriptionResponse;
import com.breaze.genesis.services.ISubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;


@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final ISubscriptionService subscriptionService;

    
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ListSubscriptionHistoryResponse>> getSubscriptionHistory(
        @Valid @ModelAttribute SubscriptionHistoryQueryRequest query,
        Authentication authentication
    ) {
        List<ListSubscriptionHistoryResponse> history = subscriptionService.getHistory(getAuthenticatedEmail(authentication), query);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MyActiveSubscriptionResponse> getMyActiveSubscription(Authentication authentication) {
        MyActiveSubscriptionRequest request = MyActiveSubscriptionRequest.builder()
                .authenticatedEmail(getAuthenticatedEmail(authentication))
                .build();
        MyActiveSubscriptionResponse response = subscriptionService.getMyActiveSubscription(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CreateSubscriptionResponse> createSubscription(
            Authentication authentication,
            @Valid @RequestBody SubscriptionCreateRequest request
    ) {
        CreateSubscriptionResponse response = subscriptionService.createSubscription(getAuthenticatedEmail(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
        }
        return authentication.getName();
    }
}
