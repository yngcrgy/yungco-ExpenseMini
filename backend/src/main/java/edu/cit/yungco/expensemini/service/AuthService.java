package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.dto.AuthRequest;
import edu.cit.yungco.expensemini.dto.AuthResponse;
import edu.cit.yungco.expensemini.dto.RegisterRequest;
import edu.cit.yungco.expensemini.model.Role;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.UserRepository;
import edu.cit.yungco.expensemini.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final EmailService emailService;

        public AuthResponse register(RegisterRequest request) {
                if (repository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email already in use");
                }

                var user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.USER) // Default role
                                .provider("LOCAL")
                                .build();

                repository.save(user);
                var jwtToken = jwtService.generateToken(user);

                // Asynchronously send welcome email (in a real app, use @Async or Message
                // Queue)
                try {
                        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
                } catch (Exception e) {
                        System.err.println("Could not send welcome email: " + e.getMessage());
                }

                return AuthResponse.builder()
                                .token(jwtToken)
                                .id(user.getId())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .role(user.getRole().name())
                                .build();
        }

        public AuthResponse authenticate(AuthRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                var user = repository.findByEmail(request.getEmail())
                                .orElseThrow();

                var jwtToken = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .id(user.getId())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .role(user.getRole().name())
                                .build();
        }

        public void processForgotPassword(String email) {
                // 1. Check if the user exists
                Optional<User> userOptional = repository.findByEmail(email);

                if (userOptional.isPresent()) {
                        User user = userOptional.get();

                        // 2. Generate a secure reset token (you'd normally save this to the DB)
                        // String resetToken = UUID.randomUUID().toString();
                        // createPasswordResetTokenForUser(user, resetToken);

                        // 3. Send the email (Requires an EmailService)
                        // String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;
                        // emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

                        System.out.println("Password reset requested for: " + email);
                } else {
                        // Security Best Practice: Don't reveal if an email exists or not.
                        // Just fail silently or log it.
                        System.out.println("Forgot password requested for non-existent email: " + email);
                }
        }
}
