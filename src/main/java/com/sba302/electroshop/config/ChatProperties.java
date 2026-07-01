package com.sba302.electroshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình cho module chat (đọc từ application.properties, prefix {@code app.chat}).
 */
@Configuration
@ConfigurationProperties(prefix = "app.chat")
@Data
public class ChatProperties {

    /** Câu chào tự động gửi khi khách nhắn tin đầu tiên. */
    private String autoReplyMessage =
            "Cảm ơn bạn đã liên hệ ElectroShop. Nhân viên sẽ phản hồi sớm nhất có thể.";
}
