package com.sba302.electroshop.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class VNPayUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private VNPayUtil() {}

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot compute HMAC-SHA512", e);
        }
    }

    public static String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> sorted = new TreeMap<>(params);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (!sb.isEmpty()) sb.append("&");
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }
        return sb.toString();
    }

    public static String buildHashData(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> sorted = new TreeMap<>(params);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (!sb.isEmpty()) sb.append("&");
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }
        return sb.toString();
    }

    public static String generateTxnRef(Integer orderId) {
        return orderId + "_" + LocalDateTime.now().format(FORMATTER);
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}

