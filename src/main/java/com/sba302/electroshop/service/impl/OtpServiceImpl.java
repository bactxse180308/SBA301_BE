package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_VALIDITY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateAndStoreOtp(String email) {
        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        String key = OTP_PREFIX + email;
        
        redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(OTP_VALIDITY_MINUTES));
        log.info("Generated new OTP for email: {}", email);
        
        return otp;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);
        
        if (storedOtp != null && storedOtp.equals(otp)) {
            // OTP matches, delete it so it can't be used again
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    @Override
    public void invalidateOtp(String email) {
        String key = OTP_PREFIX + email;
        redisTemplate.delete(key);
        log.info("Invalidated OTP for email: {}", email);
    }
}
