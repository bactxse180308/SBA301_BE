package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.AuthResponse;

public interface OAuth2Service {
    AuthResponse verifyGoogleToken(String idTokenString);
}
