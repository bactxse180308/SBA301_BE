package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreatePriceTierRequest;
import com.sba302.electroshop.dto.request.UpdatePriceTierRequest;
import com.sba302.electroshop.dto.response.BulkPriceTierResponse;
import com.sba302.electroshop.entity.BulkPriceTier;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.BulkPriceTierRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.service.PriceTierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class PriceTierServiceImpl implements PriceTierService {

    private final BulkPriceTierRepository priceTierRepository;
    private final ProductRepository productRepository;

    @Override
    public List<BulkPriceTierResponse> getByProductId(Integer productId) {
        return priceTierRepository
                .findByProduct_ProductIdAndIsActiveTrueOrderByMinQtyAsc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BulkPriceTierResponse create(CreatePriceTierRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        BulkPriceTier tier = BulkPriceTier.builder()
                .product(product)
                .minQty(request.getMinQty())
                .maxQty(request.getMaxQty())
                .unitPrice(request.getUnitPrice())
                .isActive(true)
                .build();

        log.info("Creating price tier for productId={}, minQty={}", request.getProductId(), request.getMinQty());
        return toResponse(priceTierRepository.save(tier));
    }

    @Override
    @Transactional
    public BulkPriceTierResponse update(Integer id, UpdatePriceTierRequest request) {
        BulkPriceTier tier = priceTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price tier not found: " + id));

        if (request.getMinQty() != null) tier.setMinQty(request.getMinQty());
        if (request.getMaxQty() != null) tier.setMaxQty(request.getMaxQty());
        if (request.getUnitPrice() != null) tier.setUnitPrice(request.getUnitPrice());
        if (request.getIsActive() != null) tier.setIsActive(request.getIsActive());

        log.info("Updating price tier id={}", id);
        return toResponse(priceTierRepository.save(tier));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        BulkPriceTier tier = priceTierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price tier not found: " + id));
        // Soft delete
        tier.setIsActive(false);
        priceTierRepository.save(tier);
        log.info("Soft-deleted price tier id={}", id);
    }

    private BulkPriceTierResponse toResponse(BulkPriceTier tier) {
        return BulkPriceTierResponse.builder()
                .bulkPriceTierId(tier.getBulkPriceTierId())
                .minQty(tier.getMinQty())
                .maxQty(tier.getMaxQty())
                .unitPrice(tier.getUnitPrice())
                .isActive(tier.getIsActive())
                .discountPercent(tier.getDiscountPercent())
                .build();
    }
}
