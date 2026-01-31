package com.sba302.electroshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Admin Configuration Properties
 * Đọc các thông tin cấu hình admin từ application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "app.admin")
@Data
public class AdminConfigProperties {

    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
}
