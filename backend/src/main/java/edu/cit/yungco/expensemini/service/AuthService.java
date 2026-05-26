package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.dto.AuthRequest;
import edu.cit.yungco.expensemini.dto.AuthResponse;
import edu.cit.yungco.expensemini.dto.RegisterRequest;
import edu.cit.yungco.expensemini.dto.ResetPasswordRequest;
import edu.cit.yungco.expensemini.model.PasswordResetToken;
import edu.cit.yungco.expensemini.model.Role;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.PasswordResetTokenRepository;
import edu.cit.yungco.expensemini.repository.UserRepository;
import edu.cit.yungco.expensemini.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        String pass = request.getPassword();
        if (pass == null || pass.length() < 8 || pass.length() > 12) {
            throw new RuntimeException("Password must be 8 to 12 characters long.");
        }
        if (!pass.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new RuntimeException("Password must contain at least one special character.");
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

        // Asynchronously send welcome email
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
        Optional<User> userOptional = repository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user, token);

            // Use localhost:5173 for web reset link
            String resetUrl = "http://localhost:5173/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

            System.out.println("Password reset email sent to: " + email);
        } else {
            System.out.println("Forgot password requested for non-existent email: " + email);
        }
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        // Delete existing token if any
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        PasswordResetToken myToken = new PasswordResetToken(token, user);
        tokenRepository.save(myToken);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getPassword();

        PasswordResetToken passToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (isTokenExpired(passToken)) {
            tokenRepository.delete(passToken);
            throw new RuntimeException("Token expired");
        }

        User user = passToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
        tokenRepository.delete(passToken);
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().before(new Date());
    }
}
