package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatus(Long userId, String status);

    Optional<Subscription> findByStripePaymentId(String stripePaymentId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Subscription s WHERE s.user.id = :userId")
    void deleteAllByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
