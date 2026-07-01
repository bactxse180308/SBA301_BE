package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.config.ChatProperties;
import com.sba302.electroshop.dto.response.ChatMessageResponse;
import com.sba302.electroshop.dto.response.ConversationResponse;
import com.sba302.electroshop.entity.ChatMessage;
import com.sba302.electroshop.entity.Conversation;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.ConversationStatus;
import com.sba302.electroshop.enums.SenderRole;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.ChatMessageRepository;
import com.sba302.electroshop.repository.ConversationRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class ChatServiceImpl implements ChatService {

    private static final String CUSTOMER_QUEUE = "/queue/messages"; // -> /user/{id}/queue/messages
    private static final String STAFF_TOPIC = "/topic/support";
    private static final int MAX_PAGE_SIZE = 100;

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatProperties chatProperties;

    // =========================================================================
    // KHÁCH HÀNG
    // =========================================================================

    @Override
    @Transactional
    public ConversationResponse getOrCreateMyConversation(Integer customerId) {
        Conversation c = getOrCreateActiveConversation(customerId);
        return ConversationResponse.from(c, unreadForCustomer(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> historyForCustomer(Integer customerId, Long beforeId, int size) {
        Conversation c = getActiveConversationOrThrow(customerId);
        return loadHistory(c, beforeId, size);
    }

    @Override
    @Transactional
    public ChatMessageResponse sendTextAsCustomer(Integer customerId, String content, Integer productId) {
        String text = content == null ? "" : content.trim();
        Product product = (productId == null) ? null
                : productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + productId));
        if (text.isBlank() && product == null) {
            throw new ApiException("Nội dung tin nhắn không được để trống");
        }

        Conversation c = getOrCreateActiveConversation(customerId);
        boolean isFirst = chatMessageRepository.countByConversation_Id(c.getId()) == 0;

        ChatMessage saved = saveMessage(c, loadUser(customerId), SenderRole.CUSTOMER, text, product);
        fanout(c, ChatMessageResponse.from(saved, false)); // tin mới: chưa đọc

        if (isFirst) {
            maybeAutoReply(c);
        }
        return ChatMessageResponse.from(saved, false);
    }

    @Override
    @Transactional
    public void markReadByCustomer(Integer customerId) {
        Conversation c = getActiveConversationOrThrow(customerId);
        latestMessageId(c).ifPresent(id -> {
            c.setLastReadByCustomerMsgId(id);
            conversationRepository.save(c);
        });
    }

    // =========================================================================
    // NHÂN VIÊN (ADMIN)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationResponse> listConversations(ConversationStatus status, Pageable pageable) {
        ConversationStatus filter = status != null ? status : ConversationStatus.OPEN;
        return conversationRepository.findByStatusOrderByLastMessageAtDesc(filter, pageable)
                .map(c -> ConversationResponse.from(c, unreadForStaff(c)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> historyForStaff(Long conversationId, Long beforeId, int size) {
        Conversation c = getConversationOrThrow(conversationId);
        return loadHistory(c, beforeId, size);
    }

    @Override
    @Transactional
    public ChatMessageResponse sendTextAsStaff(Integer staffId, Long conversationId, String content) {
        String text = content == null ? "" : content.trim();
        if (text.isBlank()) {
            throw new ApiException("Nội dung tin nhắn không được để trống");
        }
        Conversation c = getConversationOrThrow(conversationId);
        ChatMessage saved = saveMessage(c, loadUser(staffId), SenderRole.STAFF, text);
        fanout(c, ChatMessageResponse.from(saved, false));
        return ChatMessageResponse.from(saved, false);
    }

    @Override
    @Transactional
    public void markReadByStaff(Long conversationId) {
        Conversation c = getConversationOrThrow(conversationId);
        latestMessageId(c).ifPresent(id -> {
            c.setLastReadByStaffMsgId(id);
            conversationRepository.save(c);
        });
    }

    @Override
    @Transactional
    public ConversationResponse closeConversation(Long conversationId) {
        Conversation c = getConversationOrThrow(conversationId);
        c.setStatus(ConversationStatus.CLOSED);
        conversationRepository.save(c);
        return ConversationResponse.from(c, unreadForStaff(c));
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /** 1 khách = 1 hội thoại OPEN. */
    private Conversation getOrCreateActiveConversation(Integer customerId) {
        return conversationRepository
                .findFirstByCustomer_UserIdAndStatusOrderByCreatedAtDesc(customerId, ConversationStatus.OPEN)
                .orElseGet(() -> conversationRepository.save(Conversation.builder()
                        .customer(loadUser(customerId))
                        .status(ConversationStatus.OPEN)
                        .build()));
    }

    private Conversation getActiveConversationOrThrow(Integer customerId) {
        return conversationRepository
                .findFirstByCustomer_UserIdAndStatusOrderByCreatedAtDesc(customerId, ConversationStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa có hội thoại nào"));
    }

    private Conversation getConversationOrThrow(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội thoại id=" + conversationId));
    }

    private User loadUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng id=" + userId));
    }

    private ChatMessage saveMessage(Conversation c, User sender, SenderRole role, String content) {
        return saveMessage(c, sender, role, content, null);
    }

    /** Lưu tin nhắn, kèm snapshot sản phẩm nếu [product] != null. */
    private ChatMessage saveMessage(Conversation c, User sender, SenderRole role, String content, Product product) {
        ChatMessage.ChatMessageBuilder builder = ChatMessage.builder()
                .conversation(c)
                .sender(sender)
                .senderRole(role)
                .content(content);
        if (product != null) {
            builder.productId(product.getProductId())
                    .productName(product.getProductName())
                    .productImageUrl(product.getMainImage())
                    .productPrice(product.getPrice());
        }
        ChatMessage m = chatMessageRepository.save(builder.build());
        c.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(c);
        return m;
    }

    /** Lịch sử cursor (mới -> cũ), kèm trạng thái read cho từng tin. */
    private List<ChatMessageResponse> loadHistory(Conversation c, Long beforeId, int size) {
        int safe = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(0, safe);
        Page<ChatMessage> page = (beforeId == null)
                ? chatMessageRepository.findByConversation_IdOrderByIdDesc(c.getId(), pageable)
                : chatMessageRepository.findByConversation_IdAndIdLessThanOrderByIdDesc(c.getId(), beforeId, pageable);
        return page.getContent().stream()
                .map(m -> ChatMessageResponse.from(m, isRead(m, c)))
                .toList();
    }

    /** Tin đã được phía ĐỐI DIỆN đọc chưa (so với con trỏ lastReadBy...). */
    private boolean isRead(ChatMessage m, Conversation c) {
        Long ptr = (m.getSenderRole() == SenderRole.CUSTOMER)
                ? c.getLastReadByStaffMsgId()
                : c.getLastReadByCustomerMsgId();
        return ptr != null && m.getId() <= ptr;
    }

    /** Số tin của nhân viên mà khách chưa đọc. */
    private long unreadForCustomer(Conversation c) {
        long after = c.getLastReadByCustomerMsgId() != null ? c.getLastReadByCustomerMsgId() : 0L;
        return chatMessageRepository.countByConversation_IdAndSenderRoleAndIdGreaterThan(
                c.getId(), SenderRole.STAFF, after);
    }

    /** Số tin của khách mà nhân viên chưa đọc. */
    private long unreadForStaff(Conversation c) {
        long after = c.getLastReadByStaffMsgId() != null ? c.getLastReadByStaffMsgId() : 0L;
        return chatMessageRepository.countByConversation_IdAndSenderRoleAndIdGreaterThan(
                c.getId(), SenderRole.CUSTOMER, after);
    }

    private java.util.Optional<Long> latestMessageId(Conversation c) {
        return chatMessageRepository
                .findByConversation_IdOrderByIdDesc(c.getId(), PageRequest.of(0, 1))
                .getContent().stream().findFirst()
                .map(ChatMessage::getId);
    }

    /** Đẩy realtime về khách + pool nhân viên. Lỗi WS chỉ log, KHÔNG rollback. */
    private void fanout(Conversation c, ChatMessageResponse dto) {
        try {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(c.getCustomer().getUserId()), CUSTOMER_QUEUE, dto);
        } catch (MessagingException ex) {
            log.warn("WS push customer failed: {}", ex.getClass().getSimpleName());
        }
        try {
            messagingTemplate.convertAndSend(STAFF_TOPIC, dto);
        } catch (MessagingException ex) {
            log.warn("WS push staff failed: {}", ex.getClass().getSimpleName());
        }
    }

    /** Câu chào tự động khi khách gửi tin ĐẦU TIÊN. */
    private void maybeAutoReply(Conversation c) {
        String text = chatProperties.getAutoReplyMessage();
        if (text == null || text.isBlank()) {
            return;
        }
        User staff = userRepository
                .findFirstByRole_RoleNameAndStatusAndIsDeletedFalse("ADMIN", UserStatus.ACTIVE)
                .orElse(null);
        if (staff == null) {
            log.warn("Không tìm thấy nhân viên ADMIN để gửi câu chào tự động");
            return;
        }
        ChatMessage auto = saveMessage(c, staff, SenderRole.STAFF, text);
        fanout(c, ChatMessageResponse.from(auto, false));
    }
}
