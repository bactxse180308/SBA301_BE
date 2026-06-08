package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.NotificationResponse;
import com.sba302.electroshop.entity.Notification;
import com.sba302.electroshop.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "APIs for user notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Retrieve all notifications for the authenticated user")
    public ApiResponse<List<NotificationResponse>> getNotifications() {
        Integer userId = getCurrentUserId();
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        List<NotificationResponse> response = notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Retrieve the count of unread notifications for the authenticated user")
    public ApiResponse<Long> getUnreadCount() {
        Integer userId = getCurrentUserId();
        long count = notificationService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read for the authenticated user")
    public ApiResponse<Void> markAsRead(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ApiResponse.success(null);
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the authenticated user")
    public ApiResponse<Void> markAllAsRead() {
        Integer userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new com.sba302.electroshop.exception.ApiException("User not authenticated");
        }
        try {
            return Integer.parseInt(auth.getName());
        } catch (NumberFormatException e) {
            throw new com.sba302.electroshop.exception.ApiException("Invalid user identifier in token");
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType().name().toLowerCase())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
