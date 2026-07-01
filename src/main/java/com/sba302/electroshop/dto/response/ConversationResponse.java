package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.entity.Conversation;
import com.sba302.electroshop.enums.ConversationStatus;

import java.time.LocalDateTime;

/**
 * Thông tin tóm tắt một hội thoại + số tin chưa đọc của phía đang xem.
 */
public record ConversationResponse(
        Long id,
        ConversationStatus status,
        LocalDateTime lastMessageAt,
        long unreadCount,
        String customerName
) {
    public static ConversationResponse from(Conversation c, long unreadCount) {
        return new ConversationResponse(
                c.getId(),
                c.getStatus(),
                c.getLastMessageAt(),
                unreadCount,
                c.getCustomer() != null ? c.getCustomer().getFullName() : null
        );
    }
}
