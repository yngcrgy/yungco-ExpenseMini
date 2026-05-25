package edu.cit.yungco.expensemini.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import edu.cit.yungco.expensemini.dto.AuthResponse;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.UserRepository;
import edu.cit.yungco.expensemini.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    @Value("${google.client.id}")
    private String googleClientId;

    // Injecting your existing services
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse verifyGoogleTokenAndLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // This verifies signature, expiration, and audience
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // 1. Extract user info
                String email = payload.getEmail();
                String firstName = (String) payload.get("given_name");
                String lastName = (String) payload.get("family_name");

                // 2. Database check: Find user by email, or create a new one
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);

                    // Note: If your User model requires a non-null password or a specific Role
                    // (e.g. Role.USER) by default, make sure to set them here.

                    return userRepository.save(newUser);
                });

                // 3. Generate your app's JWT using your JwtService
                String appToken = jwtService.generateToken(user);

                // 4. Return success response
                // Assuming your AuthResponse takes the token in its constructor or builder
                return AuthResponse.builder()
                        .token(appToken)
                        .build();
                // Or if it uses a standard constructor: return new AuthResponse(appToken);

            } else {
                throw new RuntimeException("Invalid Google Token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token verification failed: " + e.getMessage());
        }
    }
}