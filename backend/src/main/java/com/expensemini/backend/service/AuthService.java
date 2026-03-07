package com.expensemini.backend.service;

import com.expensemini.backend.dto.AuthRequest;
import com.expensemini.backend.dto.AuthResponse;
import com.expensemini.backend.dto.RegisterRequest;
import com.expensemini.backend.model.Role;
import com.expensemini.backend.model.User;
import com.expensemini.backend.repository.UserRepository;
import com.expensemini.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .role(user.getRole())
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
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .role(user.getRole())
                                .build();
        }
}
