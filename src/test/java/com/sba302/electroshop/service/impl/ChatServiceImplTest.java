package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.config.ChatProperties;
import com.sba302.electroshop.dto.response.ChatMessageResponse;
import com.sba302.electroshop.entity.ChatMessage;
import com.sba302.electroshop.entity.Conversation;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.ConversationStatus;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.enums.SenderRole;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.ChatMessageRepository;
import com.sba302.electroshop.repository.ConversationRepository;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    private static final Integer CUSTOMER_ID = 7;

    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ChatProperties chatProperties;

    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatServiceImpl(
                conversationRepository,
                chatMessageRepository,
                userRepository,
                productRepository,
                orderRepository,
                messagingTemplate,
                chatProperties);
    }

    @Test
    void sendTextAsCustomerAttachesOwnedShippedOrder() {
        LocalDateTime orderDate = LocalDateTime.of(2026, 7, 18, 9, 30);
        User customer = customer();
        Order order = Order.builder()
                .orderId(42)
                .user(customer)
                .orderStatus(OrderStatus.SHIPPED)
                .finalAmount(new BigDecimal("1250000"))
                .orderDate(orderDate)
                .build();
        Conversation conversation = conversation(customer);

        when(orderRepository.findByOrderIdAndUser_UserId(42, CUSTOMER_ID))
                .thenReturn(Optional.of(order));
        when(conversationRepository.findFirstByCustomer_UserIdAndStatusOrderByCreatedAtDesc(
                CUSTOMER_ID, ConversationStatus.OPEN)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.countByConversation_Id(conversation.getId())).thenReturn(1L);
        when(userRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage message = invocation.getArgument(0);
            message.setId(99L);
            message.setCreatedAt(LocalDateTime.now());
            return message;
        });
        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatMessageResponse response = chatService.sendTextAsCustomer(
                CUSTOMER_ID, "", null, order.getOrderId());

        assertEquals(order.getOrderId(), response.orderId());
        assertEquals(OrderStatus.SHIPPED, response.orderStatus());
        assertEquals(order.getFinalAmount(), response.orderTotal());
        assertEquals(orderDate, response.orderDate());

        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(messageCaptor.capture());
        assertEquals(order.getOrderId(), messageCaptor.getValue().getOrderId());
    }

    @Test
    void sendTextAsCustomerHidesOrderOwnedByAnotherCustomer() {
        when(orderRepository.findByOrderIdAndUser_UserId(42, CUSTOMER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> chatService.sendTextAsCustomer(CUSTOMER_ID, "", null, 42));

        verifyNoInteractions(conversationRepository, chatMessageRepository);
    }

    @Test
    void sendTextAsCustomerRejectsOrderThatIsNotShipped() {
        Order pendingOrder = Order.builder()
                .orderId(42)
                .user(customer())
                .orderStatus(OrderStatus.PENDING)
                .build();
        when(orderRepository.findByOrderIdAndUser_UserId(42, CUSTOMER_ID))
                .thenReturn(Optional.of(pendingOrder));

        assertThrows(
                ApiException.class,
                () -> chatService.sendTextAsCustomer(CUSTOMER_ID, "", null, 42));

        verifyNoInteractions(conversationRepository, chatMessageRepository);
    }

    @Test
    void historyForCustomerResolvesCurrentOrderStatus() {
        User customer = customer();
        Conversation conversation = conversation(customer);
        ChatMessage message = ChatMessage.builder()
                .id(99L)
                .conversation(conversation)
                .sender(customer)
                .senderRole(SenderRole.CUSTOMER)
                .content("")
                .orderId(42)
                .createdAt(LocalDateTime.now())
                .build();
        Order deliveredOrder = Order.builder()
                .orderId(42)
                .orderStatus(OrderStatus.DELIVERED)
                .finalAmount(new BigDecimal("1250000"))
                .orderDate(LocalDateTime.of(2026, 7, 18, 9, 30))
                .build();

        when(conversationRepository.findFirstByCustomer_UserIdAndStatusOrderByCreatedAtDesc(
                CUSTOMER_ID, ConversationStatus.OPEN)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.findByConversation_IdOrderByIdDesc(
                any(Long.class), any())).thenReturn(new PageImpl<>(List.of(message)));
        when(orderRepository.findAllById(any())).thenReturn(List.of(deliveredOrder));

        List<ChatMessageResponse> history = chatService.historyForCustomer(
                CUSTOMER_ID, null, 20);

        assertEquals(1, history.size());
        assertEquals(OrderStatus.DELIVERED, history.get(0).orderStatus());
    }

    private User customer() {
        return User.builder()
                .userId(CUSTOMER_ID)
                .fullName("Khách hàng")
                .build();
    }

    private Conversation conversation(User customer) {
        return Conversation.builder()
                .id(3L)
                .customer(customer)
                .status(ConversationStatus.OPEN)
                .build();
    }
}
