package com.breaze.genesis.service;

import com.breaze.genesis.dto.response.PlanSummaryResponse;
import com.breaze.genesis.entity.Plan;
import com.breaze.genesis.entity.Subscription;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exception.BusinessException;
import com.breaze.genesis.exception.ResourceNotFoundException;
import com.breaze.genesis.repository.PlanRepository;
import com.breaze.genesis.repository.SubscriptionRepository;
import com.breaze.genesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Política decidida: al cambiar de plan, se conserva el saldo anterior y se suman los tokens del nuevo plan.
     */
    @Transactional
    public SubscriptionResponse subscribe(String email, SubscriptionRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new BusinessException("El plan está inactivo");
        }

        // Desactivar suscripción activa anterior si existe
        subscriptionRepository.findByUserAndActiveTrue(user).ifPresent(activeSub -> {
            activeSub.setActive(false);
            activeSub.setEndDate(LocalDateTime.now());
            subscriptionRepository.save(activeSub);
        });

        // Crear nueva suscripción activa
        Subscription newSub = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(LocalDateTime.now())
                .active(true)
                .build();

        subscriptionRepository.save(newSub);

        // Sumar tokens al saldo
        int newBalance = (user.getTokenBalance() == null ? 0 : user.getTokenBalance()) + plan.getTokenAmount();
        user.setTokenBalance(newBalance);
        userRepository.save(user);

        return SubscriptionResponse.builder()
                .message("Suscripción creada correctamente")
                .userId(user.getId())
                .plan(PlanSummaryResponse.builder()
                        .id(plan.getId())
                        .name(plan.getName())
                        .tokenAmount(plan.getTokenAmount())
                        .build())
                .newTokenBalance(newBalance)
                .startDate(newSub.getStartDate())
                .active(true)
                .build();
    }

    @Transactional(readOnly = true)
    public MySubscriptionResponse getMyActiveSubscription(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("No tienes una suscripción activa"));

        return MySubscriptionResponse.builder()
                .id(sub.getId())
                .plan(PlanSummaryResponse.builder()
                        .id(sub.getPlan().getId())
                        .name(sub.getPlan().getName())
                        .tokenAmount(sub.getPlan().getTokenAmount())
                        .build())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .active(sub.getActive())
                .build();
    }
}