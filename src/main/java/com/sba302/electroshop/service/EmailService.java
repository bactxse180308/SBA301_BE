package com.sba302.electroshop.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendWelcomeEmail(String toEmail, String fullName, String companyName, String rawPassword);
}
