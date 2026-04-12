package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.plan.dto.PlanItemDTO;
import com.breaze.genesis.dto.plan.requests.DeletePlanRequest;
import com.breaze.genesis.dto.plan.requests.ListPlansRequest;
import com.breaze.genesis.dto.plan.requests.UpdatePlanRequest;
import com.breaze.genesis.dto.plan.responses.DeletePlanResponse;
import com.breaze.genesis.dto.plan.responses.ListPlansResponse;
import com.breaze.genesis.dto.plan.responses.UpdatePlanResponse;
import com.breaze.genesis.entity.plan.Plan;
import com.breaze.genesis.entity.plan.PlanVersion;
import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.exceptions.BusinessException;
import com.breaze.genesis.exceptions.ResourceNotFoundException;
import com.breaze.genesis.repository.plan.IPlanRepository;
import com.breaze.genesis.repository.plan.IPlanVersionRepository;
import com.breaze.genesis.repository.subscription.ISubscriptionRepository;
import com.breaze.genesis.services.IPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * Implementacion del servicio de planes con versionado temporal.
 *
 * @version 1.0.0
 * @author donpedromz
 */
public class PlanServiceImpl implements IPlanService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;
    private static final String DEFAULT_SORT = "name,asc";

    private final IPlanRepository planRepository;
    private final IPlanVersionRepository planVersionRepository;
    private final ISubscriptionRepository subscriptionRepository;

    /**
     * Obtiene el listado paginado del catalogo de planes.
     *
     * @param request parametros de paginacion y ordenamiento
     * @return pagina de planes con disponibilidad actual
     */
    @Override
    @Transactional(readOnly = true)
    public ListPlansResponse getPlans(ListPlansRequest request) {
        PageRequest pageRequest = buildPageRequest(request);
        LocalDateTime now = LocalDateTime.now();

        Page<Plan> page = planRepository.findAll(pageRequest);
        List<PlanItemDTO> content = page.getContent().stream()
                .map(plan -> mapPlan(plan, now))
                .toList();

        return ListPlansResponse.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * Actualiza los datos base del plan y crea una nueva version con vigencia inmediata.
     *
     * @param planId identificador del plan a actualizar
     * @param request datos del plan y de su nueva version
     * @return detalle de la version creada
     */
    @Override
    @Transactional
    public UpdatePlanResponse updatePlan(Long planId, UpdatePlanRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        validateUniqueName(request.getPlanName(), planId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resolvedVersionValidTo = resolveVersionValidTo(request.getVersionValidTo(), now);

        List<PlanVersion> effectiveVersions = planVersionRepository.findEffectiveVersions(planId, now, PageRequest.of(0, 1));
        if (!effectiveVersions.isEmpty()) {
            PlanVersion currentVersion = effectiveVersions.get(0);
            
            if (isIdempotentUpdate(plan, currentVersion, request, resolvedVersionValidTo)) {
                return buildIdempotentResponse(plan, currentVersion, now);
            }
        }

        closeCurrentEffectiveVersion(planId, now);

        // valid_from is always "now" to avoid scheduled versions in this stage.
        plan.setName(request.getPlanName().trim());
        plan.setDescription(request.getPlanDescription().trim());
        Plan savedPlan = planRepository.save(plan);

        PlanVersion newVersion = planVersionRepository.save(
                PlanVersion.builder()
                        .plan(savedPlan)
                        .tokenLimit(request.getTokenLimitPerCycle())
                        .durationSeconds(request.getBillingCycleDurationSeconds())
                        .validFrom(now)
                        .validTo(resolvedVersionValidTo)
                        .build()
        );

        syncActiveSubscriptionsForNextRenewal(savedPlan.getId(), savedPlan);

        return UpdatePlanResponse.builder()
                .planId(savedPlan.getId())
                .planName(savedPlan.getName())
                .planDescription(savedPlan.getDescription())
                .tokenLimitPerCycle(newVersion.getTokenLimit())
                .billingCycleDurationSeconds(newVersion.getDurationSeconds())
                .versionValidFrom(newVersion.getValidFrom())
                .versionValidTo(newVersion.getValidTo())
                .currentlyAvailable(isVersionEffective(newVersion, now))
                .build();
    }

    /**
     * Elimina un plan cuando cumple las precondiciones de negocio.
     *
     * @param request solicitud con el identificador del plan
     * @return resultado de eliminacion
     */
    @Override
    @Transactional
    public DeletePlanResponse deletePlan(DeletePlanRequest request) {
        Long planId = request.getPlanId();
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        if (subscriptionRepository.existsByPlanVersionPlanIdAndStatus(planId, SubscriptionStatus.ACTIVE)) {
            throw new BusinessException("No se puede eliminar el plan porque tiene suscripciones activas");
        }

        if (subscriptionRepository.existsByPlanVersionPlanId(planId)) {
            throw new BusinessException("No se puede eliminar el plan porque tiene historial de suscripciones");
        }

        planVersionRepository.deleteByPlanId(planId);
        planRepository.delete(plan);

        return DeletePlanResponse.builder()
                .planId(planId)
                .deleted(true)
                .message("Plan eliminado exitosamente")
                .build();
    }

    /**
     * Mapea la entidad de plan a su DTO de respuesta para listado.
     *
     * @param plan plan del catalogo
     * @param now marca de tiempo de evaluacion
     * @return dto del item de plan
     */
    private PlanItemDTO mapPlan(Plan plan, LocalDateTime now) {
        List<PlanVersion> effectiveVersions = planVersionRepository.findEffectiveVersions(
                plan.getId(),
                now,
                PageRequest.of(0, 1)
        );

        Optional<PlanVersion> latestVersion = planVersionRepository.findFirstByPlanIdOrderByValidFromDesc(plan.getId());

        if (effectiveVersions.isEmpty()) {
            return PlanItemDTO.builder()
                .planId(plan.getId())
                .planName(plan.getName())
                .planDescription(plan.getDescription())
                .tokenLimitPerCycle(latestVersion.map(PlanVersion::getTokenLimit).orElse(null))
                .billingCycleDurationSeconds(latestVersion.map(PlanVersion::getDurationSeconds).orElse(null))
                .build();
        }

        PlanVersion activeVersion = effectiveVersions.get(0);
        return PlanItemDTO.builder()
            .planId(plan.getId())
            .planName(plan.getName())
            .planDescription(plan.getDescription())
            .tokenLimitPerCycle(activeVersion.getTokenLimit())
            .billingCycleDurationSeconds(activeVersion.getDurationSeconds())
            .build();
    }

    private PageRequest buildPageRequest(ListPlansRequest request) {
        int page = resolvePage(request.getPage());
        int size = resolveSize(request.getSize());
        Sort sort = resolveSort(request.getSort());
        return PageRequest.of(page, size, sort);
    }

    private int resolvePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int resolveSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private Sort resolveSort(String rawSort) {
        String sortExpression = (rawSort == null || rawSort.isBlank()) ? DEFAULT_SORT : rawSort;
        String[] parts = sortExpression.split(",");
        String property = parts[0].trim().isEmpty() ? "name" : parts[0].trim();
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }

    /**
     * Valida unicidad de nombre para evitar duplicados en el catalogo.
     *
     * @param name nombre propuesto
     * @param planId id del plan que se esta editando
     */
    private void validateUniqueName(String name, Long planId) {
        planRepository.findByName(name.trim())
                .filter(existing -> !existing.getId().equals(planId))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un plan con el nombre indicado");
                });
    }

    /**
     * Cierra la version vigente actual del plan estableciendo validTo en el instante actual.
     *
     * @param planId id del plan
     * @param now marca de cierre
     */
    private void closeCurrentEffectiveVersion(Long planId, LocalDateTime now) {
        List<PlanVersion> effectiveVersions = planVersionRepository.findEffectiveVersions(planId, now, PageRequest.of(0, 1));
        if (effectiveVersions.isEmpty()) {
            return;
        }

        PlanVersion currentVersion = effectiveVersions.get(0);
        currentVersion.setValidTo(now);
        planVersionRepository.save(currentVersion);
    }

    /**
     * Determina el valor final de cierre para la nueva version.
     *
     * @param requestedValidTo fecha de cierre solicitada por API
     * @param versionValidFrom inicio de vigencia de la nueva version
     * @return fecha de cierre validada o null si no se define cierre
     */
    private LocalDateTime resolveVersionValidTo(LocalDateTime requestedValidTo, LocalDateTime versionValidFrom) {
        if (requestedValidTo == null) {
            return null;
        }

        if (!requestedValidTo.isAfter(versionValidFrom)) {
            throw new BusinessException("versionValidTo debe ser posterior al inicio de vigencia de la nueva versión");
        }

        return requestedValidTo;
    }

    /**
     * Evalua si una version de plan se encuentra vigente en un instante dado.
     *
     * @param version version a evaluar
     * @param at instante de referencia
     * @return true si la version esta vigente; false en caso contrario
     */
    private boolean isVersionEffective(PlanVersion version, LocalDateTime at) {
        boolean starts = !version.getValidFrom().isAfter(at);
        boolean notEnded = version.getValidTo() == null || version.getValidTo().isAfter(at);
        return starts && notEnded;
    }

    /**
     * Sincroniza las suscripciones activas para que adopten el plan actualizado al renovar.
     *
     * @param planId identificador del plan actualizado
     * @param plan entidad de plan actualizada
     */
    private void syncActiveSubscriptionsForNextRenewal(Long planId, Plan plan) {
        List<Subscription> activeSubscriptions = subscriptionRepository
                .findByPlanVersionPlanIdAndStatus(planId, SubscriptionStatus.ACTIVE);

        if (activeSubscriptions.isEmpty()) {
            return;
        }

        activeSubscriptions.forEach(subscription -> subscription.setPendingPlan(plan));
        subscriptionRepository.saveAll(activeSubscriptions);
    }

    /**
     * Verifica si la actualizacion solicitada es idempotente validando campos y la version activa.
     *
     * @param plan entidad de plan actual
     * @param currentVersion version actualmente activa del plan
     * @param request nueva solicitud
     * @param resolvedVersionValidTo fecha de fin resuelta
     * @return true si la solicitud no presenta cambios, false en caso contrario
     */
    private boolean isIdempotentUpdate(Plan plan, PlanVersion currentVersion, UpdatePlanRequest request, LocalDateTime resolvedVersionValidTo) {
        boolean sameName = plan.getName().equals(request.getPlanName().trim());
        boolean sameDescription = plan.getDescription().equals(request.getPlanDescription().trim());
        boolean sameTokenLimit = currentVersion.getTokenLimit().equals(request.getTokenLimitPerCycle());
        boolean sameDuration = currentVersion.getDurationSeconds().equals(request.getBillingCycleDurationSeconds());
        
        boolean sameValidTo = (currentVersion.getValidTo() == null && resolvedVersionValidTo == null) ||
                              (currentVersion.getValidTo() != null && currentVersion.getValidTo().equals(resolvedVersionValidTo));
        
        return sameName && sameDescription && sameTokenLimit && sameDuration && sameValidTo;
    }

    /**
     * Construye la respuesta devolviendo los valores del plan actual por ser una operacion idempotente.
     *
     * @param plan entidad de plan actual
     * @param currentVersion version de plan vigente
     * @param now instante de referencia de ejecucion
     * @return respuesta DTO formateada
     */
    private UpdatePlanResponse buildIdempotentResponse(Plan plan, PlanVersion currentVersion, LocalDateTime now) {
        return UpdatePlanResponse.builder()
                .planId(plan.getId())
                .planName(plan.getName())
                .planDescription(plan.getDescription())
                .tokenLimitPerCycle(currentVersion.getTokenLimit())
                .billingCycleDurationSeconds(currentVersion.getDurationSeconds())
                .versionValidFrom(currentVersion.getValidFrom())
                .versionValidTo(currentVersion.getValidTo())
                .currentlyAvailable(isVersionEffective(currentVersion, now))
                .build();
    }
}
