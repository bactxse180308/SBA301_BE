package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Body khi gửi một tin nhắn chat.
 * content có thể rỗng nếu đính kèm sản phẩm hoặc đơn hàng — kiểm tra ở service.
 */
public record SendMessageRequest(
        @Size(max = 2000, message = "Tin nhắn tối đa 2000 ký tự")
        String content,
        Integer productId,
        Integer orderId
) {
}
