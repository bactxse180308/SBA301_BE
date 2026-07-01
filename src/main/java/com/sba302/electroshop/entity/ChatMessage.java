package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.SenderRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * ChatMessage: một tin nhắn text trong hội thoại.
 * id tự tăng (auto-increment) đóng vai trò cursor & thứ tự thời gian.
 */
@Entity
@Table(name = "CHAT_MESSAGES", indexes = {
        @Index(name = "ix_chat_messages_conv_id", columnList = "conversation_id, chat_message_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_role", length = 20, nullable = false)
    private SenderRole senderRole;

    @Nationalized
    @Column(name = "content", length = 2000, nullable = false)
    private String content;

    // ── Đính kèm sản phẩm (snapshot tại thời điểm gửi) ──────────────────────
    // Null nếu tin nhắn không đính kèm sản phẩm. Lưu snapshot để card chat ổn
    // định kể cả khi sản phẩm về sau đổi giá/đổi tên/bị xoá.
    @Column(name = "product_id")
    private Integer productId;

    @Nationalized
    @Column(name = "product_name", length = 500)
    private String productName;

    @Column(name = "product_image_url", length = 1000)
    private String productImageUrl;

    @Column(name = "product_price")
    private java.math.BigDecimal productPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
