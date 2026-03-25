package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.OrderDetail;

import com.sba302.electroshop.dto.response.StockTransactionResponse;
import com.sba302.electroshop.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface StockTransactionService {

    /**
     * Lấy danh sách lịch sử giao dịch kho (có phân trang, filter)
     */
    Page<StockTransactionResponse> getTransactions(Integer branchId, TransactionType type, Integer orderId, Integer bulkOrderId, Pageable pageable);

    /**
     * Ghi nhận RESERVED khi đặt hàng thành công.
     * Mỗi branch được group vào 1 StockTransaction riêng.
     */
    void recordReserved(Integer orderId, List<OrderDetail> details);

    /**
     * Ghi nhận EXPORT khi warehouse xác nhận xuất kho cho order hoặc bulk order.
     */
    void recordExport(Integer orderId, Integer bulkOrderId, Integer branchId, List<ExportLine> lines);

    /**
     * Ghi nhận RELEASED hoặc IMPORT khi cancel order, tùy theo trạng thái xuất kho.
     * - Branch đã có EXPORT → INSERT IMPORT (hoàn kho thực tế)
     * - Branch chỉ có RESERVED chưa xuất → INSERT RELEASED (giải phóng reservation)
     * Trong cả 2 TH, `BranchProductStock.quantity` đều được +qty.
     */
    void recordCancellation(Integer orderId, List<OrderDetail> details);

    /**
     * DTO dùng trong recordExport.
     */
    record ExportLine(Integer productId, Integer quantity, BigDecimal price) {}
}
