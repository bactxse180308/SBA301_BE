package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.SendMessageRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.ChatMessageResponse;
import com.sba302.electroshop.dto.response.ConversationResponse;
import com.sba302.electroshop.enums.ConversationStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API chat của NHÂN VIÊN (ADMIN): xem danh sách hội thoại, trả lời, đánh dấu đã đọc, đóng hội thoại.
 */
@RestController
@RequestMapping("/api/v1/admin/chat")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Chat (Admin)", description = "Nhân viên ElectroShop quản lý & trả lời hội thoại")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    @Operation(summary = "Danh sách hội thoại")
    public ApiResponse<Page<ConversationResponse>> listConversations(
            @RequestParam(defaultValue = "OPEN") ConversationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(
                chatService.listConversations(status, PageRequest.of(page, size)));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "Lịch sử tin nhắn của một hội thoại (mới -> cũ)")
    public ApiResponse<List<ChatMessageResponse>> getMessages(
            @PathVariable Long id,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(chatService.historyForStaff(id, before, size));
    }

    @PostMapping("/conversations/{id}/messages")
    @Operation(summary = "Trả lời khách hàng")
    public ApiResponse<ChatMessageResponse> reply(
            @PathVariable Long id,
            @Valid @RequestBody SendMessageRequest request) {
        return ApiResponse.success(chatService.sendTextAsStaff(getCurrentUserId(), id, request.content()));
    }

    @PatchMapping("/conversations/{id}/read")
    @Operation(summary = "Đánh dấu nhân viên đã đọc")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        chatService.markReadByStaff(id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/conversations/{id}/close")
    @Operation(summary = "Đóng hội thoại")
    public ApiResponse<ConversationResponse> close(@PathVariable Long id) {
        return ApiResponse.success(chatService.closeConversation(id));
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
