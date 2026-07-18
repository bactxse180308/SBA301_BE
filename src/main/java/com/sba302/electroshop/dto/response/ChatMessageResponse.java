package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.entity.ChatMessage;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.enums.SenderRole;

import java.math.BigDecimal;
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
        BigDecimal productPrice,
        // orderId là link; ba field còn lại là dữ liệu live từ Order.
        Integer orderId,
        OrderStatus orderStatus,
        BigDecimal orderTotal,
        LocalDateTime orderDate,
        boolean read,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage m, boolean read) {
        return from(m, read, null);
    }

    public static ChatMessageResponse from(ChatMessage m, boolean read, Order order) {
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
                m.getOrderId(),
                order != null ? order.getOrderStatus() : null,
                order != null ? order.getFinalAmount() : null,
                order != null ? order.getOrderDate() : null,
                read,
                m.getCreatedAt()
        );
    }
}
