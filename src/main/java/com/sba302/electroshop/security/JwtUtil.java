package com.sba302.electroshop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // PHẢI trùng với IAM
    private static final String SECRET_KEY = "ThayDoiThanhChuoiBimatRatDai1234567890";

    private static final long ACCESS_EXPIRATION = 1000L * 60 * 60;           // 1 giờ
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 ngày

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateAccessToken(Integer userId, String email, List<String> roles, List<String> privileges) {
        return generateToken(userId.toString(), email, roles, privileges, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(Integer userId, String email, List<String> roles, List<String> privileges) {
        return generateToken(userId.toString(), email, roles, privileges, REFRESH_EXPIRATION);
    }

    private String generateToken(String userId,
                                 String email,
                                 List<String> roles,
                                 List<String> privileges,
                                 long expirationMillis) {

        long now = System.currentTimeMillis();

        List<String> safeRoles = roles != null ? roles : Collections.emptyList();
        List<String> safePrivileges = privileges != null ? privileges : Collections.emptyList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("roles", safeRoles);

        // ⭐ ĐỂ TƯƠNG THÍCH CẢ HAI:
        // - IAM hiện đang dùng "authorities"
        // - Về lâu dài bạn có thể dùng "privileges"
        claims.put("authorities", safePrivileges);
        claims.put("privileges", safePrivileges);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");
        if (roles instanceof List<?>) {
            return ((List<?>) roles)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<String> extractPrivileges(String token) {
        Claims claims = extractAllClaims(token);

        // ⭐ ƯU TIÊN "privileges", nếu không có thì fallback "authorities"
        Object raw = claims.get("privileges");
        if (raw == null) {
            raw = claims.get("authorities");
        }

        if (raw instanceof List<?>) {
            return ((List<?>) raw)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        // (Nếu bạn có case lưu dưới dạng String "A,B,C" thì có thể parse thêm ở đây)
        return Collections.emptyList();
    }

    public long getRemainingMinutes(String token) {
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();
        long diffMillis = expiration.getTime() - now;

        return diffMillis > 0 ? diffMillis / (1000 * 60) : 0;
    }
}
