package com.breaze.genesis.repository;

import com.breaze.genesis.entity.Subscription;
import com.breaze.genesis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserAndActiveTrue(User user);

    boolean existsByPlanIdAndActiveTrue(Long planId);
}