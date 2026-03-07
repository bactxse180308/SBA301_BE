package com.sba302.electroshop.enums;

public enum CompanyStatus {
    PENDING,          // Chờ admin duyệt
    APPROVED,         // Đã được duyệt, hoạt động bình thường
    REJECTED,         // Bị từ chối
    NEED_DOCUMENTS    // Cần bổ sung thêm tài liệu/thông tin
}
