package com.sba302.electroshop.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.sba302.electroshop.dto.response.AuthResponse;
import com.sba302.electroshop.entity.Role;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.repository.RoleRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.security.JwtUtil;
import com.sba302.electroshop.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class OAuth2ServiceImpl implements OAuth2Service {

    @Value("${app.oauth2.google.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse verifyGoogleToken(String idTokenString) {
        log.info("Verifying Google ID Token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                // String pictureUrl = (String) payload.get("picture"); // Can be used later if User entity adds an avatar field

                log.info("Verified successful for email: {}", email);

                return processUserLogin(email, name);

            } else {
                throw new ApiException("Invalid Google ID token.");
            }
        } catch (Exception e) {
            log.error("Google verify failed", e);
            throw new ApiException("Token verification failed: " + e.getMessage());
        }
    }

    private AuthResponse processUserLogin(String email, String name) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new ApiException("User account is inactive.");
            }
        } else {
            // Register new user
            Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                    .orElseThrow(() -> new ApiException("Role CUSTOMER not found"));

            String randomSecurePassword = generateRandomPassword();

            user = User.builder()
                    .email(email)
                    .fullName(name)
                    .password(randomSecurePassword) // Db requires this not to be null
                    .status(UserStatus.ACTIVE)
                    .role(customerRole)
                    .rewardPoint(0)
                    .build();

            user = userRepository.save(user);
            log.info("Created new user via Google OAuth2: {}", email);
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), Collections.singletonList(user.getRole().getRoleName()), Collections.emptyList());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getEmail(), Collections.singletonList(user.getRole().getRoleName()), Collections.emptyList());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getRoleName())
                .build();
    }

    private String generateRandomPassword() {
        // Generate a 16 chars random password for security since password can't be null
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        SecureRandom random = new SecureRandom();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
