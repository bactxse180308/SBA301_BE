package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.ForgotPasswordRequest;
import com.sba302.electroshop.dto.request.LoginRequest;
import com.sba302.electroshop.dto.request.RefreshTokenRequest;
import com.sba302.electroshop.dto.request.RegisterRequest;
import com.sba302.electroshop.dto.request.ResetPasswordRequest;
import com.sba302.electroshop.dto.response.AuthResponse;
import com.sba302.electroshop.dto.response.TokenResponse;
import com.sba302.electroshop.entity.Role;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.RoleRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.security.JwtUtil;
import com.sba302.electroshop.service.AuthService;
import com.sba302.electroshop.service.EmailService;
import com.sba302.electroshop.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final EmailService emailService;
        private final OtpService otpService;

        @Value("${app.api-base-url:http://localhost:8080}")
        private String apiBaseUrl;

        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                log.info("Registering new user with email: {}", request.getEmail());

                // Check if email already exists
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new ApiException("Email already registered");
                }

                // Get CUSTOMER role
                Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                                .orElseThrow(() -> new ApiException("CUSTOMER role not found"));

                // Create new user (formerly Customer)
                User user = User.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .fullName(request.getFullName())
                                .phoneNumber(request.getPhoneNumber())
                                .role(customerRole)
                                .status(UserStatus.PENDING) // Set to PENDING to require OTP activation
                                .isEmailVerified(false)     // Set to false to require OTP activation
                                .rewardPoint(0)
                                .registrationDate(LocalDateTime.now())
                                .build();

                user = userRepository.save(user);

                log.info("User registered successfully with ID: {}. Triggering automatic OTP generation.", user.getUserId());

                // Automatically generate and send OTP email upon successful registration
                try {
                    otpService.invalidateOtp(user.getEmail());
                    String otp = otpService.generateAndStoreOtp(user.getEmail());
                    emailService.sendOtpEmail(user.getEmail(), otp);
                    log.info("Successfully sent registration OTP to: {}", user.getEmail());
                } catch (Exception e) {
                    log.error("Failed to send registration OTP email to: {}", user.getEmail(), e);
                }

                return AuthResponse.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().getRoleName())
                                .build();
        }

        @Override
        public AuthResponse login(LoginRequest request) {
                log.info("Login attempt for email: {}", request.getEmail());

                // Find user by email
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

                // Check if user is active
                if (user.getStatus() != UserStatus.ACTIVE) {
                        throw new ApiException("Account is disabled or pending.");
                }

                // Verify password
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new ResourceNotFoundException("Invalid email or password");
                }

                log.info("User logged in successfully: {}", user.getUserId());

                // Generate tokens
                List<String> roles = Collections.singletonList(user.getRole().getRoleName());
                List<String> privileges = Collections.emptyList();

                String accessToken = jwtUtil.generateAccessToken(
                                user.getUserId(),
                                user.getEmail(),
                                roles,
                                privileges);

                String refreshToken = jwtUtil.generateRefreshToken(
                                user.getUserId(),
                                user.getEmail(),
                                roles,
                                privileges);

                return AuthResponse.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().getRoleName())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .expiresIn(3600L) // 1 hour in seconds
                                .companyId(user.getCompany() != null ? user.getCompany().getCompanyId() : null)
                                .build();
        }

        @Override
        @Transactional
        public void verifyEmail(String token) {
                log.info("Verifying email with token: {}", token);

                User user = userRepository.findByVerificationToken(token)
                                .orElseThrow(() -> new ApiException("Invalid or expired verification token"));

                user.setIsEmailVerified(true);
                user.setStatus(UserStatus.ACTIVE);
                user.setVerificationToken(null); // Clear the token

                userRepository.save(user);
                log.info("Email verified successfully for user: {}", user.getEmail());
        }

        @Override
        public TokenResponse refreshToken(RefreshTokenRequest request) {
                String refreshToken = request.getRefreshToken();

                // Validate refresh token
                if (!jwtUtil.validateToken(refreshToken)) {
                        throw new ApiException("Invalid or expired refresh token");
                }

                // Extract user info from refresh token
                String userIdStr = jwtUtil.extractUserId(refreshToken);
                Integer userId = Integer.parseInt(userIdStr); // userId stored as String in token, parse back to Integer
                String email = jwtUtil.extractEmail(refreshToken);
                List<String> roles = jwtUtil.extractRoles(refreshToken);
                List<String> privileges = jwtUtil.extractPrivileges(refreshToken);

                log.info("Refreshing token for user: {}", userId);

                // Verify user still exists and is active
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (user.getStatus() != UserStatus.ACTIVE) {
                        throw new ApiException("Account is disabled");
                }

                // Generate new tokens
                String newAccessToken = jwtUtil.generateAccessToken(
                                user.getUserId(),
                                user.getEmail(),
                                roles,
                                privileges);

                String newRefreshToken = jwtUtil.generateRefreshToken(
                                user.getUserId(),
                                user.getEmail(),
                                roles,
                                privileges);

                return TokenResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .expiresIn(3600L) // 1 hour in seconds
                                .build();
        }

        @Override
        @Transactional
        public void forgotPassword(ForgotPasswordRequest request) {
                String email = request.getEmail();
                log.info("Request forgot password for email: {}", email);

                // Check if user exists
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

                if (user.getIsDeleted()) {
                        throw new ApiException("User account is deleted.");
                }

                // Generate and send OTP for forgot password
                try {
                    otpService.invalidateOtp(email);
                    String otp = otpService.generateAndStoreOtp(email);
                    emailService.sendOtpEmail(email, otp);
                    log.info("Sent forgot password OTP to: {}", email);
                } catch (Exception e) {
                    log.error("Failed to send forgot password OTP to: {}", email, e);
                    throw new ApiException("Failed to send OTP. Please try again.");
                }
        }

        @Override
        @Transactional
        public void resetPassword(ResetPasswordRequest request) {
                String email = request.getEmail();
                String otp = request.getOtp();
                String newPassword = request.getNewPassword();
                log.info("Resetting password for email: {}", email);

                // Verify OTP
                boolean isValid = otpService.verifyOtp(email, otp);
                if (!isValid) {
                        throw new ApiException("Invalid or expired OTP.");
                }

                // Update password
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

                user.setPassword(passwordEncoder.encode(newPassword));
                
                // Verify email and activate if it was pending
                user.setIsEmailVerified(true);
                user.setStatus(UserStatus.ACTIVE);
                
                userRepository.save(user);
                log.info("Password reset successfully for email: {}", email);
        }
}
