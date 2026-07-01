package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.entity.ChatMessage;
import com.sba302.electroshop.enums.SenderRole;

import java.time.LocalDateTime;

/**
 * Payload tin nhắn dùng CHUNG cho cả REST response và WebSocket push.
 * {@code read} = tin này đã được phía đối diện đọc chưa.
 */
public record ChatMessageResponse(
        Long id,
        Long conversationId,
        SenderRole senderRole,
        String senderName,
        String senderAvatarUrl,
        String content,
        // Snapshot sản phẩm đính kèm (null nếu không đính kèm).
        Integer productId,
        String productName,
        String productImageUrl,
        java.math.BigDecimal productPrice,
        boolean read,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage m, boolean read) {
        return new ChatMessageResponse(
                m.getId(),
                m.getConversation().getId(),
                m.getSenderRole(),
                m.getSender().getFullName(),
                null, // User entity hiện chưa lưu avatar -> để null, Flutter fallback chữ cái đầu
                m.getContent(),
                m.getProductId(),
                m.getProductName(),
                m.getProductImageUrl(),
                m.getProductPrice(),
                read,
                m.getCreatedAt()
        );
    }
}
