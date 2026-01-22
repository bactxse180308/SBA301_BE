package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private Integer userId;
    private String email;
    private String fullName;
    private String role;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // in seconds
}
