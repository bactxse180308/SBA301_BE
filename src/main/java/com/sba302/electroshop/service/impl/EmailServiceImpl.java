package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP Code - ElectroShop");
            message.setText("Hello,\n\nYour OTP code is: " + otp + "\n\nThis code is valid for 5 minutes. Please do not share this code with anyone.\n\nBest regards,\nElectroShop Team");
            
            javaMailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            // In a production environment, you might want to handle this more robustly (e.g., retry mechanisms)
            // For now, we just log the error so the async process doesn't crash the calling thread.
        }
    }
}
