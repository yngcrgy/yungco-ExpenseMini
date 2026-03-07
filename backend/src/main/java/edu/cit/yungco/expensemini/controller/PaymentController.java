package edu.cit.yungco.expensemini.controller;

import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @AuthenticationPrincipal User user) {
        String checkoutUrl = paymentService.createCheckoutSession(user);
        return ResponseEntity.ok(Map.of("url", checkoutUrl));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(
            @RequestParam String sessionId) {
        paymentService.verifyPayment(sessionId);
        return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
    }
}
