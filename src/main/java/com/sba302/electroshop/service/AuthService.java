package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.LoginRequest;
import com.sba302.electroshop.dto.request.RefreshTokenRequest;
import com.sba302.electroshop.dto.request.RegisterRequest;
import com.sba302.electroshop.dto.response.AuthResponse;
import com.sba302.electroshop.dto.response.TokenResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(RefreshTokenRequest request);
}
