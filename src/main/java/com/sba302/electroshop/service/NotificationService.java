package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Notification;
import com.sba302.electroshop.enums.NotificationType;
import java.util.List;

public interface NotificationService {
    Notification createNotification(Integer userId, String title, String body, NotificationType type);
    List<Notification> getNotificationsForUser(Integer userId);
    void markAsRead(Integer notificationId, Integer userId);
    void markAllAsRead(Integer userId);
    long getUnreadCount(Integer userId);
}
