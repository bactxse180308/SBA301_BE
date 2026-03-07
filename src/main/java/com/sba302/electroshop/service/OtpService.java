package com.sba302.electroshop.service;

public interface OtpService {
    String generateAndStoreOtp(String email);
    boolean verifyOtp(String email, String otp);
    void invalidateOtp(String email);
}
