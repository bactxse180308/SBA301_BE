package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.StockExportRequest;
import com.sba302.electroshop.dto.request.StockImportRequest;
import com.sba302.electroshop.dto.response.StockCheckResult;
import com.sba302.electroshop.dto.response.StockItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {

    Page<StockItemResponse> getInventory(String keyword, Integer branchId, Pageable pageable);

    void importStock(StockImportRequest request);

    void exportStock(StockExportRequest request);

    StockCheckResult checkStock(Integer branchId, Integer productId);
}
