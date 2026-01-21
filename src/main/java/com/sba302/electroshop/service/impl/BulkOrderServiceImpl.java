package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.repository.BulkOrderRepository;
import com.sba302.electroshop.service.BulkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BulkOrderServiceImpl implements BulkOrderService {

    private final BulkOrderRepository bulkOrderRepository;

    @Override
    public List<BulkOrder> findAll() {
        return bulkOrderRepository.findAll();
    }

    @Override
    public Optional<BulkOrder> findById(Integer id) {
        return bulkOrderRepository.findById(id);
    }

    @Override
    @Transactional
    public BulkOrder save(BulkOrder bulkOrder) {
        return bulkOrderRepository.save(bulkOrder);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        bulkOrderRepository.deleteById(id);
    }
}
