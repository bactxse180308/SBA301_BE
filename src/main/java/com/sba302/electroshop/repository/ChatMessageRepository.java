package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.ChatMessage;
import com.sba302.electroshop.enums.SenderRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    long countByConversation_Id(Long conversationId);

    /** Trang đầu lịch sử (mới -> cũ). */
    Page<ChatMessage> findByConversation_IdOrderByIdDesc(Long conversationId, Pageable pageable);

    /** Các tin cũ hơn cursor {@code beforeId} (mới -> cũ). */
    Page<ChatMessage> findByConversation_IdAndIdLessThanOrderByIdDesc(
            Long conversationId, Long beforeId, Pageable pageable);

    /** Đếm tin của một phía (CUSTOMER/STAFF) có id > con trỏ đã đọc -> số tin chưa đọc. */
    long countByConversation_IdAndSenderRoleAndIdGreaterThan(
            Long conversationId, SenderRole senderRole, Long afterId);
}
