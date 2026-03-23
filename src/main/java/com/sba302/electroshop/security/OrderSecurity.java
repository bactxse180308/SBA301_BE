package com.sba302.electroshop.security;

import com.sba302.electroshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public boolean isOwner(Integer orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        try {
            String currentUserIdStr = authentication.getName();
            Integer currentUserId = Integer.parseInt(currentUserIdStr);
            return orderRepository.findById(orderId)
                    .map(order -> order.getUser().getUserId().equals(currentUserId))
                    .orElse(false);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
