package com.sba302.electroshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Integer notificationId;
    private Integer userId;
    private String title;
    private String body;
    private String type; // lowercase matching Flutter (promo, order, product, system)
    private Boolean isRead;
    private LocalDateTime createdAt;
}
