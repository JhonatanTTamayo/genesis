package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.subscriptions.requests.SubscriptionCreateRequest;
import com.breaze.genesis.dto.subscriptions.requests.MyActiveSubscriptionRequest;
import com.breaze.genesis.dto.subscriptions.requests.SubscriptionHistoryQueryRequest;
import com.breaze.genesis.dto.subscriptions.dto.SubscriptionPlanDTO;
import com.breaze.genesis.dto.subscriptions.responses.CreateSubscriptionResponse;
import com.breaze.genesis.dto.subscriptions.responses.ListSubscriptionHistoryResponse;
import com.breaze.genesis.dto.subscriptions.responses.MyActiveSubscriptionResponse;
import com.breaze.genesis.entity.plan.Plan;
import com.breaze.genesis.entity.plan.PlanVersion;
import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.entity.tokens.TokenTransaction;
import com.breaze.genesis.entity.tokens.TokenTransactionType;
import com.breaze.genesis.entity.tokens.TokenWallet;
import com.breaze.genesis.entity.User;
import com.breaze.genesis.exceptions.BusinessException;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.plan.IPlanRepository;
import com.breaze.genesis.repository.plan.IPlanVersionRepository;
import com.breaze.genesis.repository.subscription.ISubscriptionRepository;
import com.breaze.genesis.repository.token.ITokenTransactionRepository;
import com.breaze.genesis.repository.token.ITokenWalletRepository;
import com.breaze.genesis.repository.IUserRepository;
import com.breaze.genesis.services.ISubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de suscripciones con renovacion lazy y modelo ledger + snapshot.
 *
 * @version 1.0.0
 * @author donpedromz
 */
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements ISubscriptionService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;
    private static final int DEFAULT_SUBSCRIPTION_PERIOD_DAYS = 30;

    private final IUserRepository userRepository;
    private final IPlanRepository planRepository;
    private final IPlanVersionRepository planVersionRepository;
    private final ISubscriptionRepository subscriptionRepository;
    private final ITokenWalletRepository tokenWalletRepository;
    private final ITokenTransactionRepository tokenTransactionRepository;

    /**
     * Obtiene historial de suscripciones del usuario autenticado con paginacion.
     */
    @Override
    @Transactional
    public List<ListSubscriptionHistoryResponse> getHistory(String authenticatedEmail, SubscriptionHistoryQueryRequest request) {
        User user = findAuthenticatedUser(authenticatedEmail);

        // Lazy evaluation of subscription lifecycle on each request.
        evaluateLazyRenewal(user, LocalDateTime.now());

        PageRequest pageRequest = buildPageRequest(request);

        return subscriptionRepository
                .findByUserEmail(authenticatedEmail, pageRequest)
                .map(this::mapHistory)
                .getContent();
    }

    /**
     * Crea o actualiza suscripcion activa aplicando idempotencia y ledger de wallet.
     */
    @Override
    @Transactional
    public CreateSubscriptionResponse createSubscription(String authenticatedEmail, SubscriptionCreateRequest request) {
        User user = findAuthenticatedUser(authenticatedEmail);
        LocalDateTime now = LocalDateTime.now();
        Optional<Subscription> optionalActiveSubscription = evaluateLazyRenewal(user, now);
        PlanVersion targetPlanVersion = resolveRequestedPlanVersion(request.getPlanId(), now);

        if (optionalActiveSubscription.isPresent()) {
            Subscription activeSubscription = optionalActiveSubscription.get();

            if (isSamePlan(activeSubscription, targetPlanVersion)) {
                return buildResponse(
                        activeSubscription.getPlanVersion(),
                        activeSubscription,
                        getOrCreateWallet(user).getTokensAvailable()
                );
            }

            validateUpgrade(activeSubscription, targetPlanVersion);
            expireSubscription(activeSubscription, now);
        }

        Subscription newSubscription = createActiveSubscription(user, targetPlanVersion, now);

        int newBalance = applyWalletMovement(
                user,
                targetPlanVersion.getTokenLimit(),
                TokenTransactionType.SUBSCRIPTION,
                newSubscription
        );

        return buildResponse(targetPlanVersion, newSubscription, newBalance);
    }

    /**
     * Obtiene la suscripcion activa del usuario autenticado.
     */
    @Override
    @Transactional
    public MyActiveSubscriptionResponse getMyActiveSubscription(MyActiveSubscriptionRequest request) {
        User user = findAuthenticatedUser(request.getAuthenticatedEmail());
        LocalDateTime now = LocalDateTime.now();

        Subscription activeSubscription = evaluateLazyRenewal(user, now)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una suscripción activa para el usuario autenticado"));

        return mapMyActiveSubscription(activeSubscription);
    }

    /**
     * Evalua expiracion/renovacion de forma lazy en tiempo de request.
     */
    private Optional<Subscription> evaluateLazyRenewal(User user, LocalDateTime now) {
        Optional<Subscription> optionalActiveSubscription = subscriptionRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE);
        if (optionalActiveSubscription.isEmpty()) {
            return Optional.empty();
        }

        Subscription activeSubscription = optionalActiveSubscription.get();
        if (activeSubscription.getEndDate() == null || activeSubscription.getEndDate().isAfter(now)) {
            return optionalActiveSubscription;
        }

        expireSubscription(activeSubscription, now);
        
        // Auto-renew has been intentionally removed; once expired, the user falls back to no active subscription.
        return Optional.empty();
    }

    /**
     * Resuelve la version vigente del plan solicitado.
     */
    private PlanVersion resolveRequestedPlanVersion(Long planId, LocalDateTime at) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));
        return findEffectivePlanVersion(plan.getId(), at)
                .orElseThrow(() -> new BusinessException("El plan seleccionado no tiene una versión vigente y está temporalmente deshabilitado"));
    }

    private Optional<PlanVersion> findEffectivePlanVersion(Long planId, LocalDateTime at) {
        List<PlanVersion> versions = planVersionRepository.findEffectiveVersions(
                planId,
                at,
                PageRequest.of(0, 1)
        );
        if (versions.isEmpty()) {
            return Optional.empty();
        }
        
        PlanVersion version = versions.get(0);
        if (version.getValidTo() != null && !version.getValidTo().isAfter(at)) {
            return Optional.empty();
        }

        return Optional.of(version);
    }

    /**
     * Aplica movimiento sobre wallet y registra ledger con idempotencia.
     */
    private int applyWalletMovement(
            User user,
            Integer amount,
            TokenTransactionType type,
            Subscription activeSubscription
    ) {
        TokenWallet wallet = getOrCreateWallet(user);
        int normalizedAmount = normalizeAmountByType(amount, type);
        int newBalance = wallet.getTokensAvailable() + normalizedAmount;
        if (newBalance < 0) {
            throw new BusinessException("Saldo insuficiente para completar la operación");
        }

        wallet.setTokensAvailable(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        tokenWalletRepository.save(wallet);

        TokenTransaction transaction = new TokenTransaction();
        transaction.setUser(user);
        transaction.setAmount(Math.abs(amount));
        transaction.setType(type);
        String subDesc = "Subscription: " + (activeSubscription != null ? activeSubscription.getPlanVersion().getPlan().getName() : "Unknown Plan");
        transaction.setDescription(type == TokenTransactionType.SUBSCRIPTION ? subDesc : "Consumption");
        transaction.setExpiresAt(activeSubscription != null ? activeSubscription.getEndDate() : null);
        transaction.setSubscription(activeSubscription);
        tokenTransactionRepository.save(transaction);

        return newBalance;
    }

    private TokenWallet getOrCreateWallet(User user) {
        return tokenWalletRepository.findByUserId(user.getId())
                .orElseGet(() -> tokenWalletRepository.save(
                        TokenWallet.builder()
                                .user(user)
                        .tokensAvailable(0)
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
    }

    private CreateSubscriptionResponse buildResponse(
            PlanVersion planVersion,
            Subscription subscription,
            Integer tokenBalance
    ) {
        SubscriptionPlanDTO planDTO = new SubscriptionPlanDTO(
                planVersion.getPlan().getId(),
                planVersion.getPlan().getName(),
                planVersion.getTokenLimit()
        );

        CreateSubscriptionResponse response = new CreateSubscriptionResponse();
        response.setPlan(planDTO);
        response.setNewTokenBalance(tokenBalance);
        response.setStartDate(subscription.getStartDate());
        response.setEndDate(subscription.getEndDate());
        return response;
    }

    private User findAuthenticatedUser(String authenticatedEmail) {
        return userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private PageRequest buildPageRequest(SubscriptionHistoryQueryRequest request) {
        int resolvedPage = request.getPage() == null || request.getPage() < 0 ? DEFAULT_PAGE : request.getPage();
        int resolvedSize = request.getSize() == null || request.getSize() <= 0
                ? DEFAULT_SIZE
                : Math.min(request.getSize(), MAX_SIZE);
        return PageRequest.of(resolvedPage, resolvedSize, Sort.by(Sort.Direction.DESC, "startDate"));
    }

    private boolean isSamePlan(Subscription subscription, PlanVersion targetPlanVersion) {
        return subscription.getPlanVersion().getPlan().getId().equals(targetPlanVersion.getPlan().getId());
    }

    private void validateUpgrade(Subscription activeSubscription, PlanVersion requestedPlanVersion) {
        int currentPlanTokens = activeSubscription.getPlanVersion().getTokenLimit();
        int requestedPlanTokens = requestedPlanVersion.getTokenLimit();
        if (requestedPlanTokens < currentPlanTokens) {
            throw new BusinessException(
                    "No se permite cambiar a un plan de menor nivel mientras exista una suscripción activa"
            );
        }
    }

    private void expireSubscription(Subscription subscription, LocalDateTime when) {
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setEndDate(when);
        subscriptionRepository.save(subscription);
    }

    private Subscription createActiveSubscription(User user, PlanVersion planVersion, LocalDateTime startDate) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlanVersion(planVersion);
        subscription.setStartDate(startDate);
        subscription.setEndDate(resolveSubscriptionEndDate(startDate, planVersion));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return subscriptionRepository.save(subscription);
    }

    /**
     * Calcula la fecha de fin de suscripcion a partir de la duracion configurada en la version.
     *
     * @param startDate fecha de inicio
     * @param planVersion version de plan aplicada
     * @return fecha de fin calculada
     */
    private LocalDateTime resolveSubscriptionEndDate(LocalDateTime startDate, PlanVersion planVersion) {
        Integer durationSeconds = planVersion.getDurationSeconds();
        if (durationSeconds == null || durationSeconds <= 0) {
            return startDate.plusDays(DEFAULT_SUBSCRIPTION_PERIOD_DAYS);
        }
        return startDate.plusSeconds(durationSeconds);
    }

    /**
     * Normaliza el signo del monto segun el tipo de movimiento.
     *
     * @param amount monto base
     * @param type tipo de movimiento
     * @return monto normalizado
     */
    private int normalizeAmountByType(Integer amount, TokenTransactionType type) {
        int absoluteAmount = Math.abs(amount);
        if (type == TokenTransactionType.CONSUMPTION) {
            return -absoluteAmount;
        }
        return absoluteAmount;
    }

    /**
     * Mapea una suscripcion al DTO de historial.
     *
     * @param subscription suscripcion de dominio
     * @return item de historial
     */
    private ListSubscriptionHistoryResponse mapHistory(Subscription subscription) {
        return new ListSubscriptionHistoryResponse(
                subscription.getId(),
                subscription.getPlanVersion().getPlan().getName(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getStatus() == SubscriptionStatus.ACTIVE
        );
    }

    /**
     * Mapea una suscripcion al DTO de suscripcion activa.
     *
     * @param subscription suscripcion de dominio
     * @return respuesta de suscripcion activa
     */
    private MyActiveSubscriptionResponse mapMyActiveSubscription(Subscription subscription) {
        SubscriptionPlanDTO planDTO = SubscriptionPlanDTO.builder()
                .id(subscription.getPlanVersion().getPlan().getId())
                .name(subscription.getPlanVersion().getPlan().getName())
                .tokenAmount(subscription.getPlanVersion().getTokenLimit())
                .build();

        return MyActiveSubscriptionResponse.builder()
                .id(subscription.getId())
                .plan(planDTO)
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .active(subscription.getStatus() == SubscriptionStatus.ACTIVE)
                .build();
    }
}
