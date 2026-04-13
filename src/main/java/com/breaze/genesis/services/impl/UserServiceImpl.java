package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.auth.responses.PlanSummaryResponse;
import com.breaze.genesis.dto.auth.responses.UserProfileResponse;
import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.dto.users.requests.UpdateUserStatusRequest;
import com.breaze.genesis.dto.users.responses.UserListItemResponse;
import com.breaze.genesis.dto.users.responses.UserPageResponse;
import com.breaze.genesis.dto.users.responses.UserStatusResponse;
import com.breaze.genesis.entity.Role;
import com.breaze.genesis.exceptions.ForbiddenException;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.UserAdminListProjection;
import com.breaze.genesis.repository.subscription.ISubscriptionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.services.IUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService{

    private final IUserRepository userRepository;
    private final ISubscriptionRepository subscriptionRepository;
        private final ITokenWalletRepository tokenWalletRepository;

    @Override
    public UserPageResponse listUsers(Pageable pageable) {
        Page<UserAdminListProjection> users = userRepository.findUsersForAdmin(SubscriptionStatus.ACTIVE, pageable);

        return UserPageResponse.builder()
                .content(users.getContent().stream().map(this::mapUserListItem).toList())
                .page(users.getNumber())
                .size(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .last(users.isLast())
                .build();
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

    private UserListItemResponse mapUserListItem(UserAdminListProjection projection) {
        PlanSummaryResponse activePlan = null;
        if (projection.getActivePlanId() != null) {
            activePlan = PlanSummaryResponse.builder()
                    .id(projection.getActivePlanId())
                    .name(projection.getActivePlanName())
                    .tokenAmount(projection.getActivePlanTokenAmount())
                    .build();
        }

        return UserListItemResponse.builder()
                .id(projection.getUserId())
                .fullName(projection.getFullName())
                .email(projection.getEmail())
                .active(projection.getActive())
                .tokenBalance(projection.getTokenBalance())
                .activePlan(activePlan)
                .build();
    }

    @Override
    public UserStatusResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userToUpdate.getRole() == Role.ADMIN) {
            throw new ForbiddenException("Administrators cannot be deactivated or modified via this endpoint");
        }

        userToUpdate.setActive(request.getActive());
        userRepository.save(userToUpdate);

        return UserStatusResponse.builder()
                .userId(userToUpdate.getId())
                .email(userToUpdate.getEmail())
                .active(userToUpdate.getActive())
                .resultMessage("Status updated successfully")
                .build();
    }
}