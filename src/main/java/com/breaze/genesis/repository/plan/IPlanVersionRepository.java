package com.breaze.genesis.repository.plan;

import com.breaze.genesis.entity.plan.PlanVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IPlanVersionRepository extends JpaRepository<PlanVersion, Long> {

  long countByPlanId(Long planId);

    Optional<PlanVersion> findFirstByPlanIdAndValidToIsNullOrderByValidFromDesc(Long planId);

    Optional<PlanVersion> findFirstByPlanIdOrderByValidFromDesc(Long planId);

    void deleteByPlanId(Long planId);

    @Query("""
            SELECT pv
            FROM PlanVersion pv
            WHERE pv.plan.id = :planId
              AND pv.validFrom <= :at
              AND (pv.validTo IS NULL OR pv.validTo > :at)
            ORDER BY pv.validFrom DESC
            """)
    List<PlanVersion> findEffectiveVersions(@Param("planId") Long planId, @Param("at") LocalDateTime at, Pageable pageable);
}
