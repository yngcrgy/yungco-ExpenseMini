package com.expensemini.backend.repository;

import com.expensemini.backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatus(Long userId, String status);

    Optional<Subscription> findByStripePaymentId(String stripePaymentId);
}
