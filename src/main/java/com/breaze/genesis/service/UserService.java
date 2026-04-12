package com.breaze.genesis.service;

import com.breaze.genesis.dto.response.PlanSummaryResponse;
import com.breaze.genesis.dto.response.UserProfileResponse;
import com.breaze.genesis.entity.Subscription;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exception.ResourceNotFoundException;
import com.breaze.genesis.repository.SubscriptionRepository;
import com.breaze.genesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        PlanSummaryResponse activePlan = subscriptionRepository.findByUserAndActiveTrue(user)
                .map(this::mapPlan)
                .orElse(null);

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .tokenBalance(user.getTokenBalance())
                .activePlan(activePlan)
                .build();
    }

    private PlanSummaryResponse mapPlan(Subscription subscription) {
        return PlanSummaryResponse.builder()
                .id(subscription.getPlan().getId())
                .name(subscription.getPlan().getName())
                .tokenAmount(subscription.getPlan().getTokenAmount())
                .build();
    }
}