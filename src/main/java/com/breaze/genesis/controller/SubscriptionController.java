package com.breaze.genesis.controller;

import com.breaze.genesis.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(Authentication authentication,
                                                          @Valid @RequestBody SubscriptionRequest request) {
        String email = authentication.getName();
        SubscriptionResponse response = subscriptionService.subscribe(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public MySubscriptionResponse getMySubscription(Authentication authentication) {
        String email = authentication.getName();
        return subscriptionService.getMyActiveSubscription(email);
    }
}
