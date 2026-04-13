package com.breaze.genesis.repository;

import com.breaze.genesis.entity.subscriptions.SubscriptionStatus;
import com.breaze.genesis.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(
            value = """
                    SELECT u.id AS userId,
                           u.fullName AS fullName,
                           u.email AS email,
                           u.active AS active,
                           COALESCE(w.tokensAvailable, 0) AS tokenBalance,
                           p.id AS activePlanId,
                           p.name AS activePlanName,
                           pv.tokenLimit AS activePlanTokenAmount
                    FROM User u
                    LEFT JOIN u.tokenWallet w
                    LEFT JOIN Subscription s ON s.user = u AND s.status = :status
                    LEFT JOIN s.planVersion pv
                    LEFT JOIN pv.plan p
                    """,
            countQuery = "SELECT COUNT(u) FROM User u"
    )
    Page<UserAdminListProjection> findUsersForAdmin(
            @Param("status") SubscriptionStatus status,
            Pageable pageable
    );

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT u FROM User u WHERE u.id = :id")
        Optional<User> findByIdForUpdate(@Param("id") Long id);
}
