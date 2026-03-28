package com.sba302.electroshop.service;

public interface BulkOrderExportService {

    /**
     * Xuất đơn xác nhận đơn hàng (Sales Order Confirmation).
     * Chỉ ADMIN mới được gọi.
     * Thường xuất khi đơn ở trạng thái CONFIRMED.
     */
    byte[] exportOrderConfirmation(Integer bulkOrderId);

    /**
     * Xuất hóa đơn bán hàng (Invoice / VAT Invoice).
     * ADMIN hoặc chính Company sở hữu đơn đều được gọi.
     * Thường xuất khi đơn ở trạng thái SHIPPED hoặc COMPLETED.
     */
    byte[] exportInvoice(Integer bulkOrderId);
}