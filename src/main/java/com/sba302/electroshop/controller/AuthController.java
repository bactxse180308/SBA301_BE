package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.LoginRequest;
import com.sba302.electroshop.dto.request.OAuth2Request;
import com.sba302.electroshop.dto.request.RefreshTokenRequest;
import com.sba302.electroshop.dto.request.RegisterRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.AuthResponse;
import com.sba302.electroshop.dto.response.TokenResponse;
import com.sba302.electroshop.service.AuthService;
import com.sba302.electroshop.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final OAuth2Service oauth2Service;

    @PostMapping("/oauth2/google")
    @Operation(summary = "Login with Google", description = "Verify Google ID Token and authenticate user")
    public ApiResponse<AuthResponse> googleLogin(@Valid @RequestBody OAuth2Request request) {
        AuthResponse response = oauth2Service.verifyGoogleToken(request.getToken());
        return ApiResponse.success(response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and get access token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ApiResponse<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }
}
