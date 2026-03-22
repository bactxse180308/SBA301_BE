package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.CustomerWarrantyResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.CustomerWarrantyStatus;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.CustomerWarrantyRepository;
import com.sba302.electroshop.repository.WarrantyRepository;
import com.sba302.electroshop.service.CustomerWarrantyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class CustomerWarrantyServiceImpl implements CustomerWarrantyService {

    private final CustomerWarrantyRepository customerWarrantyRepository;
    private final WarrantyRepository warrantyRepository;

    // ========================================================
    // READ METHODS
    // ========================================================

    @Override
    public List<CustomerWarrantyResponse> getMyWarranties(Integer userId) {
        return customerWarrantyRepository.findByUser_UserIdOrderByEndDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerWarrantyResponse> getMyActiveWarranties(Integer userId) {
        return customerWarrantyRepository.findActiveByUserId(
                        userId, CustomerWarrantyStatus.ACTIVE, LocalDateTime.now())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerWarrantyResponse> getByOrderId(Integer orderId) {
        return customerWarrantyRepository.findByOrder_OrderId(orderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerWarrantyResponse> getByBulkOrderId(Integer bulkOrderId) {
        return customerWarrantyRepository.findByBulkOrder_BulkOrderId(bulkOrderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerWarrantyResponse> getByUserId(Integer userId) {
        return customerWarrantyRepository.findByUser_UserIdOrderByEndDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========================================================
    // WRITE METHODS
    // ========================================================

    @Override
    @Transactional
    public void createFromOrder(Order order) {
        if (order == null || order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            log.warn("createFromOrder: order is null or has no details. orderId={}",
                    order != null ? order.getOrderId() : null);
            return;
        }

        User user = order.getUser();
        LocalDateTime now = LocalDateTime.now();
        List<CustomerWarranty> warranties = order.getOrderDetails().stream()
                .filter(detail -> {
                    Integer productId = detail.getProduct().getProductId();
                    // Skip nếu đã tạo warranty cho cặp (orderId, productId) này rồi
                    if (customerWarrantyRepository.existsByOrder_OrderIdAndProduct_ProductId(
                            order.getOrderId(), productId)) {
                        log.debug("Warranty already exists for orderId={}, productId={}",
                                order.getOrderId(), productId);
                        return false;
                    }
                    return true;
                })
                .map(detail -> {
                    Product product = detail.getProduct();
                    // Tìm warranty definition cho product (lấy cái đầu tiên tìm được)
                    var warranties2 = warrantyRepository.findByProduct_ProductId(
                            product.getProductId(), PageRequest.of(0, 1));
                    if (warranties2.isEmpty()) {
                        log.debug("No warranty definition found for productId={}, skipping",
                                product.getProductId());
                        return null;
                    }
                    Warranty warrantyDef = warranties2.getContent().get(0);
                    int months = warrantyDef.getWarrantyPeriodMonths() != null
                            ? warrantyDef.getWarrantyPeriodMonths() : 0;
                    if (months <= 0) {
                        log.debug("Warranty period is 0 for productId={}, skipping",
                                product.getProductId());
                        return null;
                    }

                    LocalDateTime endDate = now.plusMonths(months);
                    return CustomerWarranty.builder()
                            .product(product)
                            .order(order)
                            .bulkOrder(null)
                            .user(user)
                            .quantity(detail.getQuantity())
                            .warrantyMonths(months)
                            .startDate(now)
                            .endDate(endDate)
                            .status(CustomerWarrantyStatus.ACTIVE)
                            .build();
                })
                .filter(cw -> cw != null)
                .collect(Collectors.toList());

        if (!warranties.isEmpty()) {
            customerWarrantyRepository.saveAll(warranties);
            log.info("Created {} customer warranties for orderId={}",
                    warranties.size(), order.getOrderId());
        }
    }

    @Override
    @Transactional
    public void createFromBulkOrder(BulkOrder bulkOrder) {
        if (bulkOrder == null || bulkOrder.getDetails() == null || bulkOrder.getDetails().isEmpty()) {
            log.warn("createFromBulkOrder: bulkOrder is null or has no details. bulkOrderId={}",
                    bulkOrder != null ? bulkOrder.getBulkOrderId() : null);
            return;
        }

        User user = bulkOrder.getUser();
        LocalDateTime now = LocalDateTime.now();
        List<CustomerWarranty> warranties = bulkOrder.getDetails().stream()
                .filter(detail -> {
                    Integer productId = detail.getProduct().getProductId();
                    if (customerWarrantyRepository.existsByBulkOrder_BulkOrderIdAndProduct_ProductId(
                            bulkOrder.getBulkOrderId(), productId)) {
                        log.debug("Warranty already exists for bulkOrderId={}, productId={}",
                                bulkOrder.getBulkOrderId(), productId);
                        return false;
                    }
                    return true;
                })
                .map(detail -> {
                    Product product = detail.getProduct();
                    var warranties2 = warrantyRepository.findByProduct_ProductId(
                            product.getProductId(), PageRequest.of(0, 1));
                    if (warranties2.isEmpty()) {
                        log.debug("No warranty definition found for productId={}, skipping",
                                product.getProductId());
                        return null;
                    }
                    Warranty warrantyDef = warranties2.getContent().get(0);
                    int months = warrantyDef.getWarrantyPeriodMonths() != null
                            ? warrantyDef.getWarrantyPeriodMonths() : 0;
                    if (months <= 0) {
                        log.debug("Warranty period is 0 for productId={}, skipping",
                                product.getProductId());
                        return null;
                    }

                    LocalDateTime endDate = now.plusMonths(months);
                    return CustomerWarranty.builder()
                            .product(product)
                            .order(null)
                            .bulkOrder(bulkOrder)
                            .user(user)
                            .quantity(detail.getQuantity())
                            .warrantyMonths(months)
                            .startDate(now)
                            .endDate(endDate)
                            .status(CustomerWarrantyStatus.ACTIVE)
                            .build();
                })
                .filter(cw -> cw != null)
                .collect(Collectors.toList());

        if (!warranties.isEmpty()) {
            customerWarrantyRepository.saveAll(warranties);
            log.info("Created {} customer warranties for bulkOrderId={}",
                    warranties.size(), bulkOrder.getBulkOrderId());
        }
    }

    @Override
    @Transactional
    public CustomerWarrantyResponse update(Integer id, String notes, CustomerWarrantyStatus status) {
        CustomerWarranty cw = customerWarrantyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer warranty not found with id: " + id));

        if (notes != null) {
            cw.setNotes(notes);
        }
        if (status != null) {
            cw.setStatus(status);
        }

        CustomerWarranty saved = customerWarrantyRepository.save(cw);
        log.info("Updated customer warranty id={}, status={}", id, saved.getStatus());
        return toResponse(saved);
    }

    // ========================================================
    // PRIVATE HELPERS
    // ========================================================

    private CustomerWarrantyResponse toResponse(CustomerWarranty cw) {
        LocalDateTime now = LocalDateTime.now();
        long daysRemaining = cw.getEndDate() != null
                ? ChronoUnit.DAYS.between(now, cw.getEndDate())
                : 0L;
        boolean isExpired = cw.getEndDate() != null && cw.getEndDate().isBefore(now);

        // Lấy main image của product
        String productImage = cw.getProduct() != null ? cw.getProduct().getMainImage() : null;

        return CustomerWarrantyResponse.builder()
                .id(cw.getId())
                .productId(cw.getProduct() != null ? cw.getProduct().getProductId() : null)
                .productName(cw.getProduct() != null ? cw.getProduct().getProductName() : null)
                .productImage(productImage)
                .orderId(cw.getOrder() != null ? cw.getOrder().getOrderId() : null)
                .bulkOrderId(cw.getBulkOrder() != null ? cw.getBulkOrder().getBulkOrderId() : null)
                .orderType(cw.getOrder() != null ? "ORDER" : "BULK_ORDER")
                .quantity(cw.getQuantity())
                .warrantyMonths(cw.getWarrantyMonths())
                .startDate(cw.getStartDate())
                .endDate(cw.getEndDate())
                .status(cw.getStatus())
                .daysRemaining(daysRemaining)
                .isExpired(isExpired)
                .notes(cw.getNotes())
                .build();
    }
}
