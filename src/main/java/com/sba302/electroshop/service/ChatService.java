package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.ChatMessageResponse;
import com.sba302.electroshop.dto.response.ConversationResponse;
import com.sba302.electroshop.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Logic chat: lưu DB là source of truth, sau đó fanout realtime qua WebSocket.
 */
public interface ChatService {

    // ===== Khách hàng =====
    ConversationResponse getOrCreateMyConversation(Integer customerId);

    List<ChatMessageResponse> historyForCustomer(Integer customerId, Long beforeId, int size);

    ChatMessageResponse sendTextAsCustomer(
            Integer customerId,
            String content,
            Integer productId,
            Integer orderId);

    void markReadByCustomer(Integer customerId);

    // ===== Nhân viên (ADMIN) =====
    Page<ConversationResponse> listConversations(ConversationStatus status, Pageable pageable);

    List<ChatMessageResponse> historyForStaff(Long conversationId, Long beforeId, int size);

    ChatMessageResponse sendTextAsStaff(Integer staffId, Long conversationId, String content);

    void markReadByStaff(Long conversationId);

    ConversationResponse closeConversation(Long conversationId);
}
