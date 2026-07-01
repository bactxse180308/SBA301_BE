package com.sba302.electroshop.config;

import com.sba302.electroshop.security.JwtUtil;
import com.sba302.electroshop.security.WsUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Xác thực JWT tại STOMP CONNECT và chặn truy cập trái phép tới topic của nhân viên.
 * - CONNECT: đọc header {@code Authorization: Bearer <token>}, validate, gắn Principal (getName()=userId).
 * - SUBSCRIBE {@code /topic/support}: chỉ cho phép ROLE_ADMIN.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String STAFF_TOPIC = "/topic/support";

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticate(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            guardStaffTopic(accessor);
        }
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MessagingException("Thiếu Authorization header tại CONNECT");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new MessagingException("Token không hợp lệ");
        }

        String userId = jwtUtil.extractUserId(token);
        List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                new WsUserPrincipal(userId), null, authorities);
        accessor.setUser(auth);
        log.debug("WS CONNECT authenticated userId={}", userId);
    }

    private void guardStaffTopic(StompHeaderAccessor accessor) {
        if (!STAFF_TOPIC.equals(accessor.getDestination())) {
            return;
        }
        Object user = accessor.getUser();
        boolean isStaff = user instanceof Authentication auth
                && auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isStaff) {
            throw new MessagingException("Chỉ nhân viên mới được subscribe " + STAFF_TOPIC);
        }
    }
}
