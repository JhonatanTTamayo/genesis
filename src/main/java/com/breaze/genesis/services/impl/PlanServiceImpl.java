package com.breaze.genesis.services.impl;

import com.breaze.genesis.dto.plan.dto.PlanItemDTO;
import com.breaze.genesis.dto.plan.requests.ListPlansRequest;
import com.breaze.genesis.dto.plan.responses.ListPlansResponse;
import com.breaze.genesis.entity.plan.Plan;
import com.breaze.genesis.entity.plan.PlanVersion;
import com.breaze.genesis.repository.plan.IPlanRepository;
import com.breaze.genesis.repository.plan.IPlanVersionRepository;
import com.breaze.genesis.services.IPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements IPlanService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;
    private static final String DEFAULT_SORT = "name,asc";

    private final IPlanRepository planRepository;
    private final IPlanVersionRepository planVersionRepository;

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

    private PlanItemDTO mapPlan(Plan plan, LocalDateTime now) {
        List<PlanVersion> effectiveVersions = planVersionRepository.findEffectiveVersions(
                plan.getId(),
                now,
                PageRequest.of(0, 1)
        );

        if (effectiveVersions.isEmpty()) {
            return PlanItemDTO.builder()
                    .id(plan.getId())
                    .name(plan.getName())
                    .tokenAmount(0)
                    .active(false)
                    .build();
        }

        PlanVersion activeVersion = effectiveVersions.get(0);
        return PlanItemDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .tokenAmount(activeVersion.getTokenLimit())
                .active(true)
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
}
