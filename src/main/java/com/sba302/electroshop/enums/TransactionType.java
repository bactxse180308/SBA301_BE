package com.sba302.electroshop.enums;

public enum TransactionType {
    IMPORT,    // Nhập kho (từ supplier hoặc hoàn trả sau cancel)
    EXPORT,    // Xuất kho (kho xác nhận giao cho shipper)
    RESERVED,  // Tồn kho được giữ khi đặt hàng
    RELEASED   // Tồn kho được giải phóng khi cancel trước khi xuất
}
