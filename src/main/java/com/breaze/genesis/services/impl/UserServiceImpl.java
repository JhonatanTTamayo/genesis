package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.auth.responses.PlanSummaryResponse;
import com.breaze.genesis.dto.auth.responses.UserProfileResponse;
import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.subscription.ISubscriptionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.services.IUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService{

    private final IUserRepository userRepository;
    private final ISubscriptionRepository subscriptionRepository;
        private final ITokenWalletRepository tokenWalletRepository;

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                Integer tokenBalance = tokenWalletRepository.findByUserId(user.getId())
                                .map(wallet -> wallet.getTokensAvailable())
                                .orElse(0);

        PlanSummaryResponse activePlan = subscriptionRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .map(this::mapPlan)
                .orElse(null);

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .tokenBalance(tokenBalance)
                .activePlan(activePlan)
                .build();
    }

    private PlanSummaryResponse mapPlan(Subscription subscription) {
        return PlanSummaryResponse.builder()
                                .id(subscription.getPlanVersion().getPlan().getId())
                                .name(subscription.getPlanVersion().getPlan().getName())
                                .tokenAmount(subscription.getPlanVersion().getTokenLimit())
                .build();
    }
}