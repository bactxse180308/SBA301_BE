package com.sba302.electroshop.config;

import tools.jackson.databind.ObjectMapper;
import com.sba302.electroshop.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ApiResponse<Object> apiResponse = ApiResponse.error(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized. Please login again or provide a valid token."
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
