package com.breaze.genesis.repository.plan;

import com.breaze.genesis.entity.plan.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IPlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByName(String name);
    boolean existsByName(String name);

    /**
     * Obtiene los planes activos verificando que su version mas reciente
     * (por fecha de vigencia) no haya caducado.
     */
    @Query("""
            SELECT DISTINCT p FROM Plan p
            JOIN PlanVersion pv ON pv.plan = p
            WHERE NOT EXISTS (
                SELECT 1 FROM PlanVersion pv2
                WHERE pv2.plan = p AND pv2.validFrom > pv.validFrom
            )
            AND (pv.validTo IS NULL OR pv.validTo > :now)
            """)
    Page<Plan> findActivePlans(@Param("now") LocalDateTime now, Pageable pageable);
}
