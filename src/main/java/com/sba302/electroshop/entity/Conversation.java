package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Conversation: một hội thoại hỗ trợ của một khách hàng với ElectroShop.
 * Quy ước: 1 khách = 1 hội thoại đang OPEN tại một thời điểm.
 */
@Entity
@Table(name = "CONVERSATIONS", indexes = {
        @Index(name = "idx_conversation_customer", columnList = "customer_id, status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private ConversationStatus status = ConversationStatus.OPEN;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    /** Con trỏ: khách đã đọc tới tin có id này. */
    @Column(name = "last_read_by_customer_msg_id")
    private Long lastReadByCustomerMsgId;

    /** Con trỏ: nhân viên đã đọc tới tin có id này. */
    @Column(name = "last_read_by_staff_msg_id")
    private Long lastReadByStaffMsgId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = ConversationStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
