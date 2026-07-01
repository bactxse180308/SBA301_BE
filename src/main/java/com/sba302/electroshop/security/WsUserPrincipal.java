package com.sba302.electroshop.security;

import java.security.Principal;

/**
 * Principal cho phiên WebSocket/STOMP. {@code getName()} trả về userId (dạng String)
 * để Spring route đúng tới đích {@code /user/{userId}/queue/messages}.
 */
public class WsUserPrincipal implements Principal {

    private final String userId;

    public WsUserPrincipal(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId;
    }
}
