package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.entity.OrderDetail;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.enums.CompanyStatus;
import com.sba302.electroshop.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    @Value("${app.frontend-url}")
    private String frontendUrl;

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
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String fullName, String companyName, String rawPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("🎉 Welcome to ElectroShop - Company Account Created");
            message.setText(
                    "Hello " + fullName + ",\n\n" +
                    "Your company account has been successfully created on ElectroShop.\n\n" +
                    "📋 Account Details:\n" +
                    "  Company : " + companyName + "\n" +
                    "  Email   : " + toEmail + "\n" +
                    "  Password: " + rawPassword + "\n\n" +
                    "⚠️  Your registration is currently PENDING review by our admin team.\n" +
                    "    You will receive another email once your account is approved.\n\n" +
                    "Please change your password after your first login.\n\n" +
                    "Best regards,\nElectroShop Team"
            );
            javaMailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    @Override
    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String verificationUrl) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🔒 Verify your email - ElectroShop");

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;\">" +
                    "    <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "        <h1 style=\"color: #2c3e50; margin-bottom: 10px;\">Welcome to <span style=\"color: #3498db;\">ElectroShop</span></h1>" +
                    "        <p style=\"color: #7f8c8d; font-size: 16px;\">Electronics Shopping Marketplace</p>" +
                    "    </div>" +
                    "    <div style=\"padding: 20px; color: #34495e; line-height: 1.6;\">" +
                    "        <p style=\"font-size: 18px;\">Hello <strong>" + fullName + "</strong>,</p>" +
                    "        <p>Thank you for choosing ElectroShop! To complete your registration and start exploring our products, please verify your email address by clicking the button below:</p>" +
                    "        <div style=\"text-align: center; margin: 30px 0;\">" +
                    "            <a href=\"" + verificationUrl + "\" style=\"background-color: #3498db; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; transition: background-color 0.3s ease-out;\">Verify My Email</a>" +
                    "        </div>" +
                    "        <p style=\"font-size: 14px; color: #95a5a6; margin-top: 20px;\">Or copy and paste this link in your browser:</p>" +
                    "        <p style=\"font-size: 14px; color: #3498db; word-break: break-all;\">" + verificationUrl + "</p>" +
                    "        <hr style=\"border: none; border-top: 1px solid #ecf0f1; margin: 30px 0;\">" +
                    "        <p style=\"margin-bottom: 5px;\">Best regards,</p>" +
                    "        <p style=\"font-weight: bold; color: #2c3e50; margin-top: 0;\">ElectroShop Team</p>" +
                    "    </div>" +
                    "    <div style=\"text-align: center; font-size: 12px; color: #95a5a6; padding-top: 20px;\">" +
                    "        &copy; 2024 ElectroShop Marketplace. All rights reserved." +
                    "    </div>" +
                    "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
        }
    }

    @Override
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("✅ Order Confirmed #" + order.getOrderId() + " - ElectroShop");

            StringBuilder itemsHtml = new StringBuilder();
            if (order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    itemsHtml.append("<tr>")
                            .append("<td style=\"padding: 10px; border-bottom: 1px solid #eee;\">")
                            .append(detail.getProduct().getProductName())
                            .append("</td>")
                            .append("<td style=\"padding: 10px; border-bottom: 1px solid #eee; text-align: center;\">")
                            .append(detail.getQuantity())
                            .append("</td>")
                            .append("<td style=\"padding: 10px; border-bottom: 1px solid #eee; text-align: right;\">")
                            .append(currencyFormatter.format(detail.getUnitPrice()))
                            .append("</td>")
                            .append("</tr>");
                }
            }

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;\">" +
                    "    <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "        <h2 style=\"color: #27ae60;\">Order Confirmed!</h2>" +
                    "        <p style=\"color: #7f8c8d;\">Your order has been confirmed and is being processed.</p>" +
                    "    </div>" +
                    "    <div style=\"padding: 10px; background-color: #f9f9f9; border-radius: 5px; margin-bottom: 20px;\">" +
                    "        <p style=\"margin: 5px 0;\"><strong>Order ID:</strong> #" + order.getOrderId() + "</p>" +
                    "        <p style=\"margin: 5px 0;\"><strong>Date:</strong> " + order.getOrderDate() + "</p>" +
                    "        <p style=\"margin: 5px 0;\"><strong>Status:</strong> <span style=\"color: #27ae60; font-weight: bold;\">CONFIRMED</span></p>" +
                    "    </div>" +
                    "    <table style=\"width: 100%; border-collapse: collapse; margin-bottom: 20px;\">" +
                    "        <thead>" +
                    "            <tr style=\"background-color: #f2f2f2;\">" +
                    "                <th style=\"padding: 10px; text-align: left;\">Product</th>" +
                    "                <th style=\"padding: 10px; text-align: center;\">Qty</th>" +
                    "                <th style=\"padding: 10px; text-align: right;\">Price</th>" +
                    "            </tr>" +
                    "        </thead>" +
                    "        <tbody>" + itemsHtml.toString() + "</tbody>" +
                    "    </table>" +
                    "    <div style=\"text-align: right; padding: 10px; font-size: 18px;\">" +
                    "        <p style=\"margin: 5px 0;\"><strong>Total Amount:</strong> <span style=\"color: #e74c3c; font-weight: bold;\">" + currencyFormatter.format(order.getFinalAmount()) + "</span></p>" +
                    "    </div>" +
                    "    <div style=\"margin-top: 20px; padding: 15px; border-left: 4px solid #3498db; background-color: #ecf0f1;\">" +
                    "        <p style=\"margin: 0; font-weight: bold;\">Shipping Address:</p>" +
                    "        <p style=\"margin: 5px 0; color: #34495e;\">" + order.getShippingAddress() + "</p>" +
                    "    </div>" +
                    "    <p style=\"margin-top: 30px; text-align: center; color: #95a5a6; font-size: 14px;\">Thank you for shopping with us!</p>" +
                    "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Order confirmation email sent for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    @Override
    @Async
    public void sendOrderCancellationEmail(Order order, String reason) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("❌ Order Cancelled #" + order.getOrderId() + " - ElectroShop");

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;\">" +
                    "    <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "        <h2 style=\"color: #e74c3c;\">Order Cancelled</h2>" +
                    "        <p style=\"color: #7f8c8d;\">We're sorry to inform you that your order has been cancelled.</p>" +
                    "    </div>" +
                    "    <div style=\"padding: 15px; background-color: #fdedec; border-radius: 5px; margin-bottom: 20px; border-left: 4px solid #e74c3c;\">" +
                    "        <p style=\"margin: 5px 0;\"><strong>Order ID:</strong> #" + order.getOrderId() + "</p>" +
                    "        <p style=\"margin: 5px 0;\"><strong>Cancellation Reason:</strong> " + (reason != null ? reason : "Not specified") + "</p>" +
                    "    </div>" +
                    "    <p style=\"color: #34495e; line-height: 1.6;\">If you have any questions or this was a mistake, please contact our support team immediately.</p>" +
                    "    <div style=\"text-align: center; margin-top: 30px;\">" +
                    "        <a href=\"#\" style=\"background-color: #3498db; color: #ffffff; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Visit Our Shop</a>" +
                    "    </div>" +
                    "    <hr style=\"border: none; border-top: 1px solid #ecf0f1; margin: 30px 0;\">" +
                    "    <p style=\"text-align: center; color: #95a5a6; font-size: 12px;\">&copy; 2024 ElectroShop Marketplace. All rights reserved.</p>" +
                    "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Order cancellation email sent for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send order cancellation email", e);
        }
    }

    @Override
    @Async
    public void sendCompanyStatusEmail(Company company, CompanyStatus status) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(company.getEmail());
            
            String subject = "";
            String title = "";
            String content = "";
            String color = "";
            String icon = "";

            switch (status) {
                case APPROVED:
                    subject = "🎉 Congratulations! Your Company Account is Approved - ElectroShop";
                    title = "Account Approved!";
                    content = "We are thrilled to inform you that your company registration for <strong>" + company.getCompanyName() + "</strong> has been approved. You can now access all our business features and start managing your products.";
                    color = "#27ae60";
                    icon = "✅";
                    break;
                case REJECTED:
                    subject = "❌ Update regarding your Company Registration - ElectroShop";
                    title = "Application Rejected";
                    content = "We regret to inform you that your company registration for <strong>" + company.getCompanyName() + "</strong> has been rejected after review. If you have any questions regarding this decision, please contact our support team.";
                    color = "#e74c3c";
                    icon = "❌";
                    break;
                case NEED_DOCUMENTS:
                    subject = "📄 Action Required: Additional Documents Needed - ElectroShop";
                    title = "Documents Needed";
                    content = "Our team has reviewed your application for <strong>" + company.getCompanyName() + "</strong> and we require some additional documents to complete the verification process. Please log in to your account to see the details and upload the required files.";
                    color = "#f39c12";
                    icon = "📄";
                    break;
                default:
                    return;
            }

            helper.setSubject(subject);

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;\">" +
                    "    <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "        <div style=\"font-size: 50px; margin-bottom: 10px;\">" + icon + "</div>" +
                    "        <h2 style=\"color: " + color + "; margin-top: 0;\">" + title + "</h2>" +
                    "        <p style=\"color: #7f8c8d; font-size: 16px;\">ElectroShop Business Platform</p>" +
                    "    </div>" +
                    "    <div style=\"padding: 20px; color: #34495e; line-height: 1.6; background-color: #f9f9f9; border-radius: 8px;\">" +
                    "        <p style=\"font-size: 18px;\">Hello <strong>" + company.getRepresentativeName() + "</strong>,</p>" +
                    "        <p>" + content + "</p>" +
                    "        <div style=\"margin: 30px 0; text-align: center;\">" +
                    "            <a href=\"#\" style=\"background-color: " + color + "; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;\">Go to Dashboard</a>" +
                    "        </div>" +
                    "    </div>" +
                    "    <div style=\"margin-top: 20px; padding: 15px; font-size: 14px; color: #7f8c8d; text-align: center;\">" +
                    "        <p>This is an automated message from the ElectroShop Verification Team.</p>" +
                    "        <p>&copy; 2024 ElectroShop Marketplace. All rights reserved.</p>" +
                    "    </div>" +
                    "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Company status email sent to {} for status: {}", company.getEmail(), status);
        } catch (Exception e) {
            log.error("Failed to send company status email", e);
        }
    }

    @Override
    @Async
    public void sendBulkOrderStatusEmail(BulkOrder bulkOrder, BulkOrderStatus status) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(bulkOrder.getUser().getEmail());
            
            String subject = "";
            String title = "";
            String content = "";
            String color = "";
            String icon = "";
            String actionText = "View Bulk Order";

            switch (status) {
                case AWAITING_PAYMENT:
                    subject = "💳 Payment Request for Bulk Order #" + bulkOrder.getBulkOrderId() + " - ElectroShop";
                    title = "Payment Required";
                    content = "Your bulk order has been confirmed! Please proceed with the payment of <strong>" + 
                              currencyFormatter.format(bulkOrder.getFinalPrice()) + "</strong> to continue processing your order.";
                    color = "#f39c12"; // Orange
                    icon = "💳";
                    actionText = "Pay Now";
                    break;
                case COMPLETED:
                    subject = "✨ Bulk Order Completed #" + bulkOrder.getBulkOrderId() + " - ElectroShop";
                    title = "Order Completed!";
                    content = "Great news! Your bulk order <strong>#" + bulkOrder.getBulkOrderId() + "</strong> has been successfully completed and delivered. Thank you for choosing ElectroShop.";
                    color = "#27ae60"; // Green
                    icon = "✨";
                    break;
                case CANCELLED:
                    subject = "❌ Bulk Order Cancelled #" + bulkOrder.getBulkOrderId() + " - ElectroShop";
                    title = "Order Cancelled";
                    content = "Your bulk order <strong>#" + bulkOrder.getBulkOrderId() + "</strong> has been cancelled. Reason: " + 
                              (bulkOrder.getCancelReason() != null ? bulkOrder.getCancelReason() : "Not specified") + ".";
                    color = "#e74c3c"; // Red
                    icon = "❌";
                    break;
                case REJECTED:
                    subject = "🚫 Bulk Order Rejected #" + bulkOrder.getBulkOrderId() + " - ElectroShop";
                    title = "Order Rejected";
                    content = "We regret to inform you that your bulk order <strong>#" + bulkOrder.getBulkOrderId() + 
                              "</strong> has been rejected after review. Reason: " + 
                              (bulkOrder.getCancelReason() != null ? bulkOrder.getCancelReason() : "Not specified") + ".";
                    color = "#c0392b"; // Dark Red
                    icon = "🚫";
                    break;
                default:
                    return; // Don't send email for other statuses unless needed
            }

            helper.setSubject(subject);

            String redirectUrl = frontendUrl + "/company/orders/" + bulkOrder.getBulkOrderId();

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;\">" +
                    "    <div style=\"text-align: center; margin-bottom: 30px;\">" +
                    "        <div style=\"font-size: 50px; margin-bottom: 10px;\">" + icon + "</div>" +
                    "        <h2 style=\"color: " + color + "; margin-top: 0;\">" + title + "</h2>" +
                    "        <p style=\"color: #7f8c8d; font-size: 16px;\">Bulk Purchasing Platform</p>" +
                    "    </div>" +
                    "    <div style=\"padding: 20px; color: #34495e; line-height: 1.6; background-color: #f9f9f9; border-radius: 8px;\">" +
                    "        <p style=\"font-size: 18px;\">Hello <strong>" + bulkOrder.getUser().getFullName() + "</strong>,</p>" +
                    "        <p>" + content + "</p>" +
                    "        <div style=\"margin: 20px 0; padding: 15px; background-color: #fff; border: 1px dashed " + color + "; border-radius: 5px;\">" +
                    "            <p style=\"margin: 5px 0;\"><strong>Bulk Order ID:</strong> #" + bulkOrder.getBulkOrderId() + "</p>" +
                    "            <p style=\"margin: 5px 0;\"><strong>Total Amount:</strong> " + currencyFormatter.format(bulkOrder.getFinalPrice()) + "</p>" +
                    "            <p style=\"margin: 5px 0;\"><strong>Company:</strong> " + bulkOrder.getCompany().getCompanyName() + "</p>" +
                    "        </div>" +
                    "        <div style=\"margin: 30px 0; text-align: center;\">" +
                    "            <a href=\"" + redirectUrl + "\" style=\"background-color: " + color + "; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;\">" + actionText + "</a>" +
                    "        </div>" +
                    "    </div>" +
                    "    <div style=\"margin-top: 20px; padding: 15px; font-size: 14px; color: #7f8c8d; text-align: center;\">" +
                    "        <p>If you have any questions, please reply to this email or contact support.</p>" +
                    "        <p>&copy; 2024 ElectroShop Marketplace. All rights reserved.</p>" +
                    "    </div>" +
                    "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Bulk order status email sent for order: {} with status: {}", bulkOrder.getBulkOrderId(), status);
        } catch (Exception e) {
            log.error("Failed to send bulk order status email", e);
        }
    }
}
