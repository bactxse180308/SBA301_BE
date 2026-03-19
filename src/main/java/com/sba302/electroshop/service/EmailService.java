package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.enums.CompanyStatus;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendWelcomeEmail(String toEmail, String fullName, String companyName, String rawPassword);
    void sendVerificationEmail(String toEmail, String fullName, String verificationUrl);
    void sendOrderConfirmationEmail(Order order);
    void sendOrderCancellationEmail(Order order, String reason);
    void sendCompanyStatusEmail(Company company, CompanyStatus status);
    void sendBulkOrderStatusEmail(BulkOrder bulkOrder, BulkOrderStatus status);
}
