package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.SendMessageRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.ChatMessageResponse;
import com.sba302.electroshop.dto.response.ConversationResponse;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API chat của KHÁCH HÀNG. Không nhận conversationId — server suy hội thoại từ JWT (chống IDOR).
 * Nhân viên (ADMIN) dùng AdminChatController; chặn ADMIN tự tạo/nhắn hội thoại như khách.
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("!hasRole('ADMIN')")
@Tag(name = "Chat (Customer)", description = "Khách hàng nhắn tin với nhân viên ElectroShop")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversation")
    @Operation(summary = "Lấy/tạo hội thoại của tôi")
    public ApiResponse<ConversationResponse> getMyConversation() {
        return ApiResponse.success(chatService.getOrCreateMyConversation(getCurrentUserId()));
    }

    @GetMapping("/conversation/messages")
    @Operation(summary = "Lịch sử tin nhắn (mới -> cũ), cursor 'before'")
    public ApiResponse<List<ChatMessageResponse>> getMessages(
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(chatService.historyForCustomer(getCurrentUserId(), before, size));
    }

    @PostMapping("/messages")
    @Operation(summary = "Gửi tin nhắn text")
    public ApiResponse<ChatMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return ApiResponse.success(chatService.sendTextAsCustomer(getCurrentUserId(), request.content(), request.productId()));
    }

    @PatchMapping("/conversation/read")
    @Operation(summary = "Đánh dấu đã đọc tới tin mới nhất")
    public ApiResponse<Void> markRead() {
        chatService.markReadByCustomer(getCurrentUserId());
        return ApiResponse.success(null);
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ApiException("User not authenticated");
        }
        try {
            return Integer.parseInt(auth.getName());
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid user identifier in token");
        }
    }
}
