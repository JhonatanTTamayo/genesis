package com.breaze.genesis.repository;

import com.breaze.genesis.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByName(String name);

    boolean existsByName(String name);
}