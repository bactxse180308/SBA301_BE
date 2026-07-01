package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Conversation;
import com.sba302.electroshop.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /** Hội thoại đang mở của một khách (1 khách = 1 hội thoại OPEN). */
    Optional<Conversation> findFirstByCustomer_UserIdAndStatusOrderByCreatedAtDesc(
            Integer customerId, ConversationStatus status);

    /** Danh sách hội thoại cho nhân viên, ưu tiên hội thoại có tin mới nhất. */
    Page<Conversation> findByStatusOrderByLastMessageAtDesc(ConversationStatus status, Pageable pageable);
}
