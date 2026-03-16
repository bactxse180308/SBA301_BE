package com.sba302.electroshop.enums;

@lombok.Getter
@lombok.AllArgsConstructor
public enum BulkOrderStatus {
    PENDING_REVIEW("Chờ duyệt"),
    CONFIRMED("Đã xác nhận"),
    AWAITING_PAYMENT("Chờ thanh toán"),
    PAID("Đã thanh toán"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đang giao"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy"),
    REJECTED("Từ chối");

    private final String description;
}
