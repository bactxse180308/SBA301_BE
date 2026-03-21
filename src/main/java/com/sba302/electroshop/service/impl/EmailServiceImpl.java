package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.enums.CompanyStatus;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine emailTemplateEngine;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Value("${app.frontend-url}")
    private String frontendUrl;

    EmailServiceImpl(JavaMailSender javaMailSender,
                     @Qualifier("emailTemplateEngine") TemplateEngine emailTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    // ======================== SIMPLE TEXT EMAILS ========================

    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Mã xác thực OTP - Phụ Kiện Điện Tử");
            message.setText("Xin chào,\n\nMã OTP của bạn: " + otp + "\n\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\nTrân trọng,\nPhụ Kiện Điện Tử");

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
            message.setSubject("🎉 Chào mừng đến Phụ Kiện Điện Tử - Tài khoản công ty đã tạo");
            message.setText(
                    "Xin chào " + fullName + ",\n\n" +
                    "Tài khoản công ty của bạn đã được tạo thành công.\n\n" +
                    "📋 Thông tin tài khoản:\n" +
                    "  Công ty  : " + companyName + "\n" +
                    "  Email    : " + toEmail + "\n" +
                    "  Mật khẩu: " + rawPassword + "\n\n" +
                    "⚠️  Tài khoản đang chờ admin phê duyệt.\n" +
                    "    Bạn sẽ nhận email khi tài khoản được phê duyệt.\n\n" +
                    "Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu.\n\n" +
                    "Trân trọng,\nPhụ Kiện Điện Tử"
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
            helper.setSubject("🔒 Xác thực email - Phụ Kiện Điện Tử");

            String htmlContent =
                "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 0; background-color: #f4f4f7;\">" +
                "  <div style=\"background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%); padding: 25px 20px; text-align: center; border-radius: 10px 10px 0 0;\">" +
                "    <table style=\"margin: 0 auto; border-collapse: collapse;\"><tr>" +
                "      <td style=\"vertical-align: middle; padding-right: 12px;\">" +
                "        <div style=\"width: 42px; height: 42px; background: rgba(255,255,255,0.2); border-radius: 8px; display: inline-block; text-align: center; line-height: 42px;\">" +
                "          <span style=\"color: #ffffff; font-weight: bold; font-size: 18px;\">PK</span>" +
                "        </div>" +
                "      </td>" +
                "      <td style=\"vertical-align: middle; text-align: left;\">" +
                "        <div style=\"color: #ffffff; font-size: 18px; font-weight: 700; line-height: 1.2;\">PHỤ KIỆN</div>" +
                "        <div style=\"color: rgba(255,255,255,0.75); font-size: 11px; letter-spacing: 1px;\">ĐIỆN TỬ</div>" +
                "      </td>" +
                "    </tr></table>" +
                "  </div>" +
                "  <div style=\"background-color: #ffffff; padding: 30px 25px; border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0;\">" +
                "    <div style=\"text-align: center; margin-bottom: 25px;\">" +
                "      <div style=\"font-size: 50px; margin-bottom: 10px;\">📧</div>" +
                "      <h2 style=\"color: #1f2937; margin: 0 0 5px 0;\">Xác thực Email</h2>" +
                "      <p style=\"color: #6b7280; font-size: 14px; margin: 0;\">Vui lòng xác thực email để hoàn tất đăng ký.</p>" +
                "    </div>" +
                "    <p style=\"font-size: 16px; color: #374151; line-height: 1.6;\">Xin chào <strong>" + fullName + "</strong>,</p>" +
                "    <p style=\"color: #374151; line-height: 1.6;\">Cảm ơn bạn đã đăng ký tại Phụ Kiện Điện Tử! Để hoàn tất đăng ký, vui lòng nhấn nút bên dưới:</p>" +
                "    <div style=\"text-align: center; margin: 30px 0;\">" +
                "      <a href=\"" + verificationUrl + "\" style=\"background: linear-gradient(135deg, #dc2626, #ef4444); color: #ffffff; padding: 14px 35px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block; font-size: 16px;\">Xác Thực Email</a>" +
                "    </div>" +
                "  </div>" +
                "  <div style=\"background-color: #1f2937; padding: 20px; text-align: center; border-radius: 0 0 10px 10px;\">" +
                "    <p style=\"color: #9ca3af; font-size: 12px; margin: 0 0 5px 0;\">Email này được gửi tự động từ hệ thống Phụ Kiện Điện Tử.</p>" +
                "    <p style=\"color: #6b7280; font-size: 12px; margin: 0;\">&copy; 2024 Phụ Kiện Điện Tử. All rights reserved.</p>" +
                "  </div>" +
                "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
        }
    }

    // ======================== TEMPLATE-BASED EMAILS ========================

    @Override
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            Context context = new Context(Locale.forLanguageTag("vi-VN"));
            context.setVariable("order", order);
            context.setVariable("orderDate", order.getOrderDate() != null
                    ? order.getOrderDate().format(DATE_FORMATTER) : "N/A");
            context.setVariable("formattedTotal", currencyFormatter.format(order.getFinalAmount()));

            String html = emailTemplateEngine.process("order-confirmation", context);
            sendHtmlEmail(order.getUser().getEmail(),
                    "✅ Đơn Hàng Đã Xác Nhận #" + order.getOrderId() + " - Phụ Kiện Điện Tử", html);

            log.info("Order confirmation email sent for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    @Override
    @Async
    public void sendOrderCancellationEmail(Order order, String reason) {
        try {
            Context context = new Context(Locale.forLanguageTag("vi-VN"));
            context.setVariable("order", order);
            context.setVariable("reason", reason);
            context.setVariable("formattedTotal", currencyFormatter.format(order.getFinalAmount()));

            String html = emailTemplateEngine.process("order-cancellation", context);
            sendHtmlEmail(order.getUser().getEmail(),
                    "❌ Đơn Hàng Đã Hủy #" + order.getOrderId() + " - Phụ Kiện Điện Tử", html);

            log.info("Order cancellation email sent for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send order cancellation email", e);
        }
    }

    @Override
    @Async
    public void sendOrderStatusEmail(Order order, OrderStatus status) {
        try {
            String subject;
            String title;
            String subtitle;
            String color;
            String icon;
            String statusLabel;
            String statusType = status.name();

            switch (status) {
                case SHIPPED:
                    subject = "🚛 Đơn hàng đang giao #" + order.getOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Đang Giao Hàng";
                    subtitle = "Đơn hàng của bạn đã được chuyển cho đơn vị vận chuyển.";
                    color = "#2563eb";
                    icon = "🚛";
                    statusLabel = "ĐANG GIAO";
                    break;
                case DELIVERED:
                    subject = "🎉 Giao hàng thành công #" + order.getOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Giao Hàng Thành Công!";
                    subtitle = "Đơn hàng đã được giao đến địa chỉ của bạn.";
                    color = "#16a34a";
                    icon = "🎉";
                    statusLabel = "ĐÃ GIAO";
                    break;
                default:
                    return;
            }

            Context context = new Context(Locale.forLanguageTag("vi-VN"));
            context.setVariable("order", order);
            context.setVariable("statusType", statusType);
            context.setVariable("title", title);
            context.setVariable("subtitle", subtitle);
            context.setVariable("color", color);
            context.setVariable("icon", icon);
            context.setVariable("statusLabel", statusLabel);
            context.setVariable("orderDate", order.getOrderDate() != null
                    ? order.getOrderDate().format(DATE_FORMATTER) : "N/A");
            context.setVariable("formattedTotal", currencyFormatter.format(order.getFinalAmount()));

            String html = emailTemplateEngine.process("order-status", context);
            sendHtmlEmail(order.getUser().getEmail(), subject, html);

            log.info("Order status email sent for order: {} with status: {}", order.getOrderId(), status);
        } catch (Exception e) {
            log.error("Failed to send order status email", e);
        }
    }

    @Override
    @Async
    public void sendCompanyStatusEmail(Company company, CompanyStatus status, String reason) {
        try {
            String subject;
            String title;
            String color;
            String icon;
            String statusLabel;

            switch (status) {
                case PENDING:
                    subject = "🏢 Yêu cầu đăng ký công ty đã gửi - Phụ Kiện Điện Tử";
                    title = "Yêu Cầu Đang Chờ Duyệt";
                    color = "#3b82f6"; // Blue
                    icon = "🏢";
                    statusLabel = "ĐANG CHỜ DUYỆT";
                    break;
                case APPROVED:
                    subject = "🎉 Chúc mừng! Tài khoản công ty đã được phê duyệt - Phụ Kiện Điện Tử";
                    title = "Tài Khoản Đã Phê Duyệt!";
                    color = "#16a34a";
                    icon = "✅";
                    statusLabel = "ĐÃ PHÊ DUYỆT";
                    break;
                case REJECTED:
                    subject = "❌ Cập nhật đăng ký công ty - Phụ Kiện Điện Tử";
                    title = "Đăng Ký Bị Từ Chối";
                    color = "#dc2626";
                    icon = "❌";
                    statusLabel = "BỊ TỪ CHỐI";
                    break;
                case NEED_DOCUMENTS:
                    subject = "📄 Yêu cầu bổ sung tài liệu - Phụ Kiện Điện Tử";
                    title = "Cần Bổ Sung Tài Liệu";
                    color = "#d97706";
                    icon = "📄";
                    statusLabel = "CẦN BỔ SUNG";
                    break;
                default:
                    return;
            }

            Context context = new Context(Locale.forLanguageTag("vi-VN"));
            context.setVariable("company", company);
            context.setVariable("status", status);
            context.setVariable("reason", reason);
            context.setVariable("title", title);
            context.setVariable("color", color);
            context.setVariable("icon", icon);
            context.setVariable("statusLabel", statusLabel);

            String html = emailTemplateEngine.process("company-status", context);
            sendHtmlEmail(company.getEmail(), subject, html);

            log.info("Company status email sent to {} for status: {}", company.getEmail(), status);
        } catch (Exception e) {
            log.error("Failed to send company status email", e);
        }
    }

    @Override
    @Async
    public void sendBulkOrderStatusEmail(BulkOrder bulkOrder, BulkOrderStatus status) {
        try {
            String subject;
            String title;
            String color;
            String icon;
            String statusLabel;

            switch (status) {
                case CONFIRMED:
                    subject = "✅ Bulk Order đã được xác nhận #" + bulkOrder.getBulkOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Đơn Hàng Đã Xác Nhận";
                    color = "#0ea5e9"; // Light blue
                    icon = "✅";
                    statusLabel = "ĐÃ TƯ VẤN & CHỐT GIÁ";
                    break;
                case AWAITING_PAYMENT:
                    subject = "💳 Yêu cầu thanh toán Bulk Order #" + bulkOrder.getBulkOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Chờ Thanh Toán";
                    color = "#d97706";
                    icon = "💳";
                    statusLabel = "CHỜ THANH TOÁN";
                    break;
                case COMPLETED:
                    subject = "✨ Bulk Order Hoàn Thành #" + bulkOrder.getBulkOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Đơn Hàng Hoàn Thành!";
                    color = "#16a34a";
                    icon = "✨";
                    statusLabel = "HOÀN THÀNH";
                    break;
                case CANCELLED:
                    subject = "❌ Bulk Order Đã Hủy #" + bulkOrder.getBulkOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Đơn Hàng Đã Hủy";
                    color = "#dc2626";
                    icon = "❌";
                    statusLabel = "ĐÃ HỦY";
                    break;
                case REJECTED:
                    subject = "🚫 Bulk Order Bị Từ Chối #" + bulkOrder.getBulkOrderId() + " - Phụ Kiện Điện Tử";
                    title = "Đơn Hàng Bị Từ Chối";
                    color = "#b91c1c";
                    icon = "🚫";
                    statusLabel = "BỊ TỪ CHỐI";
                    break;
                default:
                    return;
            }

            String reason = null;
            if (status == BulkOrderStatus.CANCELLED || status == BulkOrderStatus.REJECTED) {
                reason = bulkOrder.getCancelReason() != null ? bulkOrder.getCancelReason() : "Không có thông tin";
            }

            Context context = new Context(Locale.forLanguageTag("vi-VN"));
            context.setVariable("bulkOrder", bulkOrder);
            context.setVariable("status", status);
            context.setVariable("reason", reason);
            context.setVariable("title", title);
            context.setVariable("color", color);
            context.setVariable("icon", icon);
            context.setVariable("statusLabel", statusLabel);
            context.setVariable("formattedTotal", currencyFormatter.format(bulkOrder.getFinalPrice()));
            context.setVariable("createdDate", bulkOrder.getCreatedAt() != null
                    ? bulkOrder.getCreatedAt().format(DATE_FORMATTER) : "N/A");

            String html = emailTemplateEngine.process("bulk-order-status", context);
            sendHtmlEmail(bulkOrder.getUser().getEmail(), subject, html);

            log.info("Bulk order status email sent for order: {} with status: {}", bulkOrder.getBulkOrderId(), status);
        } catch (Exception e) {
            log.error("Failed to send bulk order status email", e);
        }
    }

    // ======================== HELPER ========================

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }
}
