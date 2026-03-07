package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.OtpSendRequest;
import com.sba302.electroshop.dto.request.OtpVerifyRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.service.EmailService;
import com.sba302.electroshop.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<String> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        String email = request.getEmail();
        
        // Ensure any old OTP is invalidated before generating a new one
        otpService.invalidateOtp(email);
        
        String otp = otpService.generateAndStoreOtp(email);
        emailService.sendOtpEmail(email, otp);

        return ApiResponse.success("OTP has been sent successfully to your email.");
    }

    @PostMapping("/verify")
    public ApiResponse<String> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        
        if (isValid) {
            return ApiResponse.success("OTP verified successfully.");
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid or expired OTP.");
        }
    }

    @PostMapping("/resend")
    public ApiResponse<String> resendOtp(@Valid @RequestBody OtpSendRequest request) {
        // Logically identical to sending, we might want rate limiting here in real app
        // For now, reuse the send flow.
        return sendOtp(request);
    }
}
