package com.breaze.genesis.repository.plan;

import com.breaze.genesis.entity.plan.Plan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IPlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByName(String name);
    boolean existsByName(String name);
}
