package com.expensemini.backend.service;

import com.expensemini.backend.model.Subscription;
import com.expensemini.backend.model.User;
import com.expensemini.backend.repository.SubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.key:sk_test_placeholder}") // Use dummy key locally if not provided
    private String stripeApiKey;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private final SubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String createCheckoutSession(User user) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/premium/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/premium/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(999L) // $9.99
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("ExpenseMini Premium Membership")
                                                                    .build())
                                                    .build())
                                    .build())
                    .setCustomerEmail(user.getEmail())
                    .build();

            Session session = Session.create(params);

            // Record Pending payment
            Subscription subscription = Subscription.builder()
                    .user(user)
                    .status("PENDING")
                    .stripePaymentId(session.getId())
                    .build();
            subscriptionRepository.save(subscription);

            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe session: " + e.getMessage());
        }
    }

    public void verifyPayment(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("paid".equals(session.getPaymentStatus())) {
                Subscription subscription = subscriptionRepository.findByStripePaymentId(sessionId)
                        .orElseThrow(() -> new RuntimeException("Subscription record not found"));

                subscription.setStatus("ACTIVE");
                subscriptionRepository.save(subscription);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verifying Stripe session: " + e.getMessage());
        }
    }
}
