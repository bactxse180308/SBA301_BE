package com.sba302.electroshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VNPayProperties {

    private String tmnCode;
    private String hashSecret;
    private String paymentUrl;
    private String returnUrl;
    private String ipnUrl;
    private String version;
    private String command;
    private String orderType;
    private String locale;
}

