package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateBulkOrderRequest;
import com.sba302.electroshop.dto.request.CreateCustomizationRequest;
import com.sba302.electroshop.dto.response.BulkOrderDetailResponse;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.dto.response.OrderCustomizationResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.enums.CustomizationStatus;
import com.sba302.electroshop.dto.response.VoucherApplicationResult;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.BulkOrderMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.BulkOrderService;
import com.sba302.electroshop.service.CustomerWarrantyService;
import com.sba302.electroshop.service.EmailService;
import com.sba302.electroshop.service.StoreBranchService;
import com.sba302.electroshop.service.StoreBranchService.StockAdjustment;
import com.sba302.electroshop.service.VoucherService;
import com.sba302.electroshop.specification.BulkOrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
class BulkOrderServiceImpl implements BulkOrderService {

    private final BulkOrderRepository bulkOrderRepository;
    private final BulkOrderDetailRepository bulkOrderDetailRepository;
    private final OrderCustomizationRepository orderCustomizationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BulkOrderMapper bulkOrderMapper;
    private final BulkOrderPricingService pricingService;
    private final VoucherService voucherService;
    private final EmailService emailService;
    private final CustomerWarrantyService customerWarrantyService;
    private final StoreBranchService storeBranchService;

    @Override
    @Transactional(readOnly = true)
    public BulkOrderResponse getById(Integer id) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));
        return buildFullResponse(bulkOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BulkOrderResponse> search(Integer userId, Integer companyId, BulkOrderStatus status,
                                          LocalDateTime createdAtFrom, LocalDateTime createdAtTo,
                                          Pageable pageable) {
        Specification<BulkOrder> spec = BulkOrderSpecification.filterBulkOrders(
                userId, companyId, status, createdAtFrom, createdAtTo);
        return bulkOrderRepository.findAll(spec, pageable)
                .map(this::buildFullResponse);
    }

    @Override
    @Transactional
    public BulkOrderResponse create(Integer userId, CreateBulkOrderRequest request) {
        // BƯỚC 1: Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // BƯỚC 2: Validate Company
        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalArgumentException("User does not belong to any company. Cannot create bulk order.");
        }

        // BƯỚC 3: Tạo BulkOrder và save ngay
        BulkOrder savedOrder = BulkOrder.builder()
                .user(user)
                .company(company)
                .createdAt(LocalDateTime.now())
                .status(BulkOrderStatus.PENDING_REVIEW)
                .subtotalAfterTier(BigDecimal.ZERO)
                .finalPrice(BigDecimal.ZERO)
                .shippingFeeWaived(false)
                .discountApplied(false)
                .shippingAddress(request.getShippingAddress())
                .build();
        savedOrder = bulkOrderRepository.save(savedOrder);

        // BƯỚC 4: Duyệt từng item, tạo detail, tính subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        List<BulkOrderDetail> detailsToSave = new ArrayList<>();
        List<OrderCustomization> customizationsToSave = new ArrayList<>();

        // BƯỚC 4: Tính toán phân bổ chi nhánh (Gợi ý, không trừ kho)
        List<StoreBranchService.AllocationRequest> allocationRequests = request.getItems().stream()
                .map(item -> new StoreBranchService.AllocationRequest(item.getProductId(), item.getQuantity()))
                .toList();
        
        StoreBranchService.AllocationResult allocationResult = storeBranchService.calculateBestAllocation(allocationRequests);
        Map<Integer, BranchProductStock> selectedStockMap = allocationResult.selectedStockMap();

        // Fetch all products at once for performance
        List<Integer> productIds = request.getItems().stream().map(CreateBulkOrderRequest.BulkOrderItemRequest::getProductId).toList();
        List<Product> products = productRepository.findAllById(productIds);
        Map<Integer, Product> productMap = new java.util.HashMap<>();
        for (Product p : products) {
            productMap.put(p.getProductId(), p);
        }

        // BƯỚC 5: Duyệt từng item, tạo detail, tính subtotal
        for (CreateBulkOrderRequest.BulkOrderItemRequest item : request.getItems()) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));
            }

            BigDecimal unitPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            BranchProductStock selectedStock = selectedStockMap.get(item.getProductId());

            BulkOrderDetail detail = BulkOrderDetail.builder()
                    .bulkOrder(savedOrder)
                    .product(product)
                    .branch(selectedStock != null ? selectedStock.getBranch() : null)
                    .quantity(item.getQuantity())
                    .unitPriceSnapshot(unitPrice)
                    .discountSnapshot(BigDecimal.ZERO)
                    .build();

            // Tính giá theo BulkPriceTier (nếu có), nếu không dùng unitPrice
            BigDecimal tierPrice = pricingService.lookupTierPrice(product.getProductId(), item.getQuantity(), unitPrice);
            detail.setAppliedTierPrice(tierPrice);
            
            detailsToSave.add(detail);

            BigDecimal customizationFeeTotal = BigDecimal.ZERO;
            if (item.getCustomizations() != null && !item.getCustomizations().isEmpty()) {
                for (CreateCustomizationRequest custReq : item.getCustomizations()) {
                    OrderCustomization customization = OrderCustomization.builder()
                            .bulkOrderDetail(detail)
                            .type(custReq.getType())
                            .note(custReq.getNote())
                            .status(CustomizationStatus.PENDING)
                            .extraFee(BigDecimal.ZERO)
                            .build();
                    customizationsToSave.add(customization);
                    customizationFeeTotal = customizationFeeTotal.add(customization.getExtraFee());
                }
            }

            BigDecimal lineTotal = tierPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
                    .add(customizationFeeTotal);
            subtotal = subtotal.add(lineTotal);
        }

        bulkOrderDetailRepository.saveAll(detailsToSave);
        if (!customizationsToSave.isEmpty()) {
            orderCustomizationRepository.saveAll(customizationsToSave);
        }

        // BƯỚC 5: Áp dụng Voucher nếu có
        String voucherCode = request.getVoucherCode();
        if (voucherCode != null && !voucherCode.isBlank()) {
            VoucherApplicationResult voucherResult = voucherService.applyVoucher(voucherCode, userId, subtotal);
            
            BigDecimal discountAmount = voucherResult.getDiscountAmount();
            BigDecimal finalPrice = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);

            savedOrder.setVoucherCode(voucherCode);
            savedOrder.setVoucherType(voucherResult.getVoucherType());
            savedOrder.setVoucherDiscountAmount(discountAmount);
            savedOrder.setFinalPrice(finalPrice);
            savedOrder.setDiscountApplied(true);

            voucherService.markVoucherAsUsed(voucherResult.getUserVoucher().getUserVoucherId());

        } else {
            savedOrder.setFinalPrice(subtotal);
            savedOrder.setDiscountApplied(false);
        }

        // BƯỚC 6: Cập nhật subtotalAfterTier, KHÔNG setDetails
        savedOrder.setSubtotalAfterTier(subtotal);
        bulkOrderRepository.save(savedOrder);

        // BƯỚC 7: Reload từ DB để lấy đầy đủ details, gửi email rồi return
        BulkOrder reloaded = bulkOrderRepository.findById(savedOrder.getBulkOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found"));
        
        return buildFullResponse(reloaded);
    }

    @Override
    @Transactional
    public BulkOrderResponse updateStatus(Integer id, BulkOrderStatus status, String note) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));

        bulkOrder.setStatus(status);
        bulkOrder.setUpdatedAt(LocalDateTime.now());

        if (status == BulkOrderStatus.CONFIRMED) {
            log.info("Order CONFIRMED. Snapshotting prices for BulkOrder ID: {}", id);
            for (BulkOrderDetail detail : bulkOrder.getDetails()) {
                BigDecimal tierPrice = detail.getAppliedTierPrice();
                if (tierPrice == null) {
                    tierPrice = pricingService.lookupTierPrice(detail.getProduct().getProductId(), detail.getQuantity(), detail.getProduct().getPrice());
                    detail.setAppliedTierPrice(tierPrice);
                }
                detail.setUnitPriceSnapshot(tierPrice);
                
                BigDecimal originalPrice = detail.getProduct().getPrice() != null ? detail.getProduct().getPrice() : tierPrice;
                detail.setDiscountSnapshot(originalPrice.subtract(tierPrice));
                
                bulkOrderDetailRepository.save(detail);
            }
        } else if (status == BulkOrderStatus.REJECTED || status == BulkOrderStatus.CANCELLED) {
            bulkOrder.setCancelReason(note);

            // Restore stock for all details in a single batch
            List<StockAdjustment> adjustments = bulkOrder.getDetails().stream()
                    .filter(d -> d.getBranch() != null)
                    .map(d -> new StockAdjustment(
                            d.getBranch().getBranchId(),
                            d.getProduct().getProductId(),
                            d.getQuantity()))
                    .toList();
            if (!adjustments.isEmpty()) {
                storeBranchService.restoreStockBatch(adjustments);
            }

            if (Boolean.TRUE.equals(bulkOrder.getDiscountApplied()) && bulkOrder.getVoucherCode() != null) {
                User user = bulkOrder.getUser();
                if (user != null) {
                    try {
                        voucherService.releaseVoucher(user.getUserId(), bulkOrder.getVoucherCode());
                        log.info("Released voucher {} for cancelled bulk order ID: {}", bulkOrder.getVoucherCode(), id);
                    } catch (Exception e) {
                        log.warn("Failed to release voucher {} for bulk order ID {}: {}", bulkOrder.getVoucherCode(), id, e.getMessage());
                    }
                }
            }
        }

        BulkOrder updated = bulkOrderRepository.save(bulkOrder);

        // Tự động tạo CustomerWarranty khi bulk order hoàn thành
        if (status == BulkOrderStatus.COMPLETED) {
            // Trigger Hibernate lazy load details (phải làm trong transaction)
            if (updated.getDetails() != null) {
                updated.getDetails().size(); // initialize collection
                for (var d : updated.getDetails()) {
                    d.getProduct().getProductId(); // initialize product proxy
                }
            }
            customerWarrantyService.createFromBulkOrder(updated);
        }

        // Send email notification for important status changes
        if (status == BulkOrderStatus.CONFIRMED || 
            status == BulkOrderStatus.AWAITING_PAYMENT || 
            status == BulkOrderStatus.COMPLETED || 
            status == BulkOrderStatus.CANCELLED || 
            status == BulkOrderStatus.REJECTED) {
            
            // Force initialize lazy proxies before passing to async EmailService
            if (updated.getUser() != null) {
                updated.getUser().getEmail();
            }
            if (updated.getDetails() != null) {
                updated.getDetails().size();
                for (com.sba302.electroshop.entity.BulkOrderDetail d : updated.getDetails()) {
                    if (d.getProduct() != null) {
                        d.getProduct().getProductName();
                        d.getProduct().getMainImage();
                    }
                    if (d.getCustomizations() != null) {
                        d.getCustomizations().size();
                    }
                }
            }
            
            emailService.sendBulkOrderStatusEmail(updated, status);
        }

        return buildFullResponse(updated);
    }

    @Override
    @Transactional
    public BulkOrderResponse addCustomization(Integer bulkOrderDetailId, CreateCustomizationRequest request) {
        BulkOrderDetail detail = bulkOrderDetailRepository.findById(bulkOrderDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order detail not found with id: " + bulkOrderDetailId));

        OrderCustomization customization = OrderCustomization.builder()
                .bulkOrderDetail(detail)
                .type(request.getType())
                .note(request.getNote())
                .status(CustomizationStatus.PENDING)
                .extraFee(BigDecimal.ZERO)
                .build();

        orderCustomizationRepository.save(customization);

        // Recalculate total price for the parent bulk order
        BulkOrder bulkOrder = detail.getBulkOrder();
        
        List<BulkOrderDetail> details = bulkOrderDetailRepository
                .findByBulkOrder_BulkOrderId(bulkOrder.getBulkOrderId());
        pricingService.recalculate(bulkOrder, details);
        
        bulkOrderRepository.save(bulkOrder);

        return buildFullResponse(bulkOrder);
    }

    @Override
    @Transactional
    public BulkOrderResponse getPriceBreakdown(Integer bulkOrderId) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(bulkOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + bulkOrderId));
        return buildFullResponse(bulkOrder);
    }

    @Override
    @Transactional
    public BulkOrderResponse reviewCustomization(Integer customizationId, String status, BigDecimal extraFee, String feeType) {
        OrderCustomization customization = orderCustomizationRepository.findById(customizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Customization not found with id: " + customizationId));

        BulkOrder bulkOrder = customization.getBulkOrderDetail().getBulkOrder();
        validateNegotiableStatus(bulkOrder);

        customization.setStatus(CustomizationStatus.valueOf(status));
        customization.setExtraFee(extraFee != null ? extraFee : BigDecimal.ZERO);
        customization.setFeeType(feeType != null ? feeType : "PER_UNIT");
        orderCustomizationRepository.save(customization);

        List<BulkOrderDetail> details = bulkOrderDetailRepository
                .findByBulkOrder_BulkOrderId(bulkOrder.getBulkOrderId());
        pricingService.recalculate(bulkOrder, details);
        bulkOrderRepository.save(bulkOrder);

        return buildFullResponse(bulkOrder);
    }

    @Override
    @Transactional
    public BulkOrderResponse updateShippingFee(Integer id, BigDecimal shippingFee) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));

        validateNegotiableStatus(bulkOrder);

        bulkOrder.setShippingFee(shippingFee != null ? shippingFee : BigDecimal.ZERO);
        
        List<BulkOrderDetail> details = bulkOrderDetailRepository
                .findByBulkOrder_BulkOrderId(bulkOrder.getBulkOrderId());
        pricingService.recalculate(bulkOrder, details);
        
        BulkOrder updated = bulkOrderRepository.save(bulkOrder);
        return buildFullResponse(updated);
    }

    private void validateNegotiableStatus(BulkOrder bulkOrder) {
        BulkOrderStatus status = bulkOrder.getStatus();
        if (status != BulkOrderStatus.PENDING_REVIEW && status != BulkOrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot update fees for order in status: " + status + ". Financial changes are only allowed in PENDING_REVIEW or CONFIRMED.");
        }
    }

    // ======================== HELPER METHODS ========================

    private BulkOrderResponse buildFullResponse(BulkOrder bulkOrder) {
        BulkOrderResponse response = bulkOrderMapper.toResponse(bulkOrder);

        List<BulkOrderDetail> details = bulkOrder.getDetails();
        if (details == null || details.isEmpty()) {
            details = bulkOrderDetailRepository.findByBulkOrder_BulkOrderId(bulkOrder.getBulkOrderId());
        }

        List<BulkOrderDetailResponse> detailResponses = new ArrayList<>();
        
        BigDecimal basePriceTotal = BigDecimal.ZERO;
        BigDecimal customizationFeeConfirmedTotal = BigDecimal.ZERO;
        BigDecimal customizationFeePendingTotal = BigDecimal.ZERO;
        boolean hasPending = false;

        for (BulkOrderDetail detail : details) {
            BulkOrderDetailResponse detailResponse = bulkOrderMapper.toDetailResponse(detail);

            // 1. Lookup applied tier price
            BigDecimal appliedTierPrice = detail.getAppliedTierPrice();
            if (appliedTierPrice == null) {
                appliedTierPrice = pricingService.lookupTierPrice(
                    detail.getProduct().getProductId(), 
                    detail.getQuantity(), 
                    detail.getUnitPriceSnapshot()
                );
            }
            detailResponse.setAppliedTierPrice(appliedTierPrice);
            detailResponse.setTierLabel(pricingService.buildTierLabel(detail, appliedTierPrice));
            
            // 2. Base price calculation
            BigDecimal itemBasePrice = detail.getProduct().getPrice() != null ? detail.getProduct().getPrice() : BigDecimal.ZERO;
            detailResponse.setBasePrice(itemBasePrice);
            basePriceTotal = basePriceTotal.add(itemBasePrice.multiply(BigDecimal.valueOf(detail.getQuantity())));

            // 3. Customization fees
            BigDecimal feeConfirmed = pricingService.calculateCustomizationFeeByStatus(detail, CustomizationStatus.APPROVED);
            BigDecimal feePending = pricingService.calculateCustomizationFeeByStatus(detail, CustomizationStatus.PENDING);
            
            detailResponse.setCustomizationFeeConfirmed(feeConfirmed);
            detailResponse.setCustomizationFeePending(feePending);

            customizationFeeConfirmedTotal = customizationFeeConfirmedTotal.add(feeConfirmed);
            customizationFeePendingTotal = customizationFeePendingTotal.add(feePending);

            // 4. Customization details (populate totalFee)
            if (detailResponse.getCustomizations() != null) {
                for (int i = 0; i < detail.getCustomizations().size(); i++) {
                    OrderCustomization entity = detail.getCustomizations().get(i);
                    OrderCustomizationResponse dto = detailResponse.getCustomizations().get(i);
                    dto.setTotalFee(pricingService.calculateCustomizationTotalFee(entity, detail.getQuantity()));
                    
                    if (entity.getStatus() == CustomizationStatus.PENDING) {
                        hasPending = true;
                    }
                }
            }

            // 5. Calculate line total = quantity * appliedTierPrice + feeConfirmed
            BigDecimal lineTotal = appliedTierPrice
                    .multiply(BigDecimal.valueOf(detail.getQuantity()))
                    .add(feeConfirmed);
            detailResponse.setLineTotal(lineTotal);

            detailResponses.add(detailResponse);
        }

        response.setDetails(detailResponses);
        
        // Populate order level breakdown
        response.setBasePriceTotal(basePriceTotal);
        response.setTierDiscountTotal(basePriceTotal.subtract(bulkOrder.getSubtotalAfterTier() != null ? bulkOrder.getSubtotalAfterTier() : BigDecimal.ZERO));
        response.setCustomizationFeeConfirmed(customizationFeeConfirmedTotal);
        response.setCustomizationFeePending(customizationFeePendingTotal);
        response.setHasPendingCustomization(hasPending);

        return response;
    }

    @Override
    @Transactional
    public BulkOrderResponse cancel(Integer id, String reason) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));

        // Only allow cancellation for certain statuses
        BulkOrderStatus currentStatus = bulkOrder.getStatus();
        if (currentStatus != BulkOrderStatus.PENDING_REVIEW && 
            currentStatus != BulkOrderStatus.CONFIRMED && 
            currentStatus != BulkOrderStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Cannot cancel order in " + currentStatus + " status");
        }

        // Reuse updateStatus logic which handles voucher release and status update
        return updateStatus(id, BulkOrderStatus.CANCELLED, reason);
    }
}
