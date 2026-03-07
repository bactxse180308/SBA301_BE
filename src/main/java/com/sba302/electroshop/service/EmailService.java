package com.sba302.electroshop.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}
