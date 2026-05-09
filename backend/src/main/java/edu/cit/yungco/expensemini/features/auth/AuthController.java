package edu.cit.yungco.expensemini.features.auth;

import edu.cit.yungco.expensemini.features.auth.AuthRequest;
import edu.cit.yungco.expensemini.features.auth.AuthResponse;
import edu.cit.yungco.expensemini.features.auth.RegisterRequest;
import edu.cit.yungco.expensemini.features.user.User;
import edu.cit.yungco.expensemini.features.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Note: For production, specify exact frontend URL
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
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
}
