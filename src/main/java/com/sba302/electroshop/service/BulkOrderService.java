package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.BulkOrder;
import java.util.List;
import java.util.Optional;

public interface BulkOrderService {
    List<BulkOrder> findAll();

    Optional<BulkOrder> findById(Integer id);

    BulkOrder save(BulkOrder bulkOrder);

    void deleteById(Integer id);
}
