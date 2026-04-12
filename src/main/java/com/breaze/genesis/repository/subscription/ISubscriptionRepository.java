package com.breaze.genesis.repository.subscription;

import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ISubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndStatus(User user, SubscriptionStatus status);

    Optional<Subscription> findByUserEmailAndStatus(String email, SubscriptionStatus status);

    Page<Subscription> findByUserEmail(String email, Pageable pageable);
}
