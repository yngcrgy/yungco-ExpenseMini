package edu.cit.yungco.expensemini.controller;

import edu.cit.yungco.expensemini.dto.AuthRequest;
import edu.cit.yungco.expensemini.dto.AuthResponse;
import edu.cit.yungco.expensemini.dto.RegisterRequest;
// import edu.cit.yungco.expensemini.dto.GoogleLoginRequest;
import edu.cit.yungco.expensemini.dto.GoogleLoginRequest;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.service.*;
// import edu.cit.yungco.expensemini.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import edu.cit.yungco.expensemini.dto.ForgotPasswordRequest;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Note: For production, specify exact frontend URL
public class AuthController {

    private final AuthService authService;
    // ADD THIS LINE to inject the GoogleAuthService:
    private final GoogleAuthService googleAuthService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody GoogleLoginRequest request) {
        try {
            AuthResponse response = googleAuthService.verifyGoogleTokenAndLogin(request.getToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("{\"error\": \"Invalid Google Token\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            // We will create this method in the AuthService next
            authService.processForgotPassword(request.getEmail());

            // Return a simple success message
            return ResponseEntity
                    .ok("{\"message\": \"If an account exists for that email, a reset link has been sent.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing your request.\"}");
        }
    }
}