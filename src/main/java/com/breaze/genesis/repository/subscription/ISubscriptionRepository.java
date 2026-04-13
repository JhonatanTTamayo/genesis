package com.breaze.genesis.repository.subscription;

import com.breaze.genesis.entity.subscriptions.Subscription;
import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ISubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndStatus(User user, SubscriptionStatus status);

    List<Subscription> findAllByUserAndStatusOrderByStartDateDesc(User user, SubscriptionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        FROM Subscription s
        WHERE s.user = :user AND s.status = :status
        ORDER BY s.startDate DESC
        """)
    List<Subscription> findAllByUserAndStatusForUpdate(
        @Param("user") User user,
        @Param("status") SubscriptionStatus status
    );

    Optional<Subscription> findByUserEmailAndStatus(String email, SubscriptionStatus status);

    Page<Subscription> findByUserEmail(String email, Pageable pageable);

    boolean existsByPlanVersionPlanIdAndStatus(Long planId, SubscriptionStatus status);

    boolean existsByPlanVersionPlanId(Long planId);

    List<Subscription> findByPlanVersionPlanIdAndStatus(Long planId, SubscriptionStatus status);
}
