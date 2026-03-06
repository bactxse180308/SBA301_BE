package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateBulkOrderRequest;
import com.sba302.electroshop.dto.request.CreateCustomizationRequest;
import com.sba302.electroshop.dto.response.BulkOrderDetailResponse;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.enums.CustomizationStatus;
import com.sba302.electroshop.enums.VoucherStatus;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.BulkOrderMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.BulkOrderService;
import com.sba302.electroshop.specification.BulkOrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class BulkOrderServiceImpl implements BulkOrderService {

    private final BulkOrderRepository bulkOrderRepository;
    private final BulkOrderDetailRepository bulkOrderDetailRepository;
    private final BulkPriceTierRepository bulkPriceTierRepository;
    private final OrderCustomizationRepository orderCustomizationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final BulkOrderMapper bulkOrderMapper;

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
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        // BƯỚC 3: Tạo BulkOrder và save ngay
        BulkOrder savedOrder = BulkOrder.builder()
                .user(user)
                .company(company)
                .createdAt(LocalDateTime.now())
                .status(BulkOrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .finalPrice(BigDecimal.ZERO)
                .discountApplied(false)
                .build();
        savedOrder = bulkOrderRepository.save(savedOrder);

        // BƯỚC 4: Duyệt từng item, tạo detail, tính subtotal
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CreateBulkOrderRequest.BulkOrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            BigDecimal unitPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;

            BulkOrderDetail detail = BulkOrderDetail.builder()
                    .bulkOrder(savedOrder)
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPriceSnapshot(unitPrice)
                    .discountSnapshot(BigDecimal.ZERO)
                    .build();

            BulkOrderDetail savedDetail = bulkOrderDetailRepository.save(detail);

            // Tính giá theo BulkPriceTier (nếu có), nếu không dùng unitPrice
            BigDecimal tierPrice = lookupTierPrice(savedDetail);
            BigDecimal lineTotal = tierPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        // BƯỚC 5: Áp dụng Voucher nếu có
        String voucherCode = request.getVoucherCode();
        if (voucherCode != null && !voucherCode.isBlank()) {
            // 5.1 Tìm voucher theo code
            Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher không hợp lệ: " + voucherCode));

            // 5.2 Validate thời hạn
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getValidFrom() != null && now.isBefore(voucher.getValidFrom())) {
                throw new IllegalArgumentException("Voucher chưa có hiệu lực");
            }
            if (voucher.getValidTo() != null && now.isAfter(voucher.getValidTo())) {
                throw new IllegalArgumentException("Voucher đã hết hạn");
            }

            // 5.3 Validate UserVoucher
            UserVoucher userVoucher = userVoucherRepository
                    .findByUser_UserIdAndVoucher_VoucherCode(userId, voucherCode)
                    .orElseThrow(() -> new ResourceNotFoundException("User không có voucher này"));

            if (userVoucher.getStatus() == VoucherStatus.USED) {
                throw new IllegalArgumentException("Voucher đã được sử dụng");
            }

            // 5.4 Tính giảm giá
            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal discountPercentage = BigDecimal.ZERO;

            if ("PERCENTAGE".equalsIgnoreCase(voucher.getDiscountType())) {
                discountPercentage = voucher.getDiscountValue();
                discountAmount = subtotal.multiply(discountPercentage)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            } else if ("FIXED_AMOUNT".equalsIgnoreCase(voucher.getDiscountType())) {
                discountAmount = voucher.getDiscountValue();
            }

            BigDecimal finalPrice = subtotal.subtract(discountAmount);
            if (finalPrice.compareTo(BigDecimal.ZERO) < 0) finalPrice = BigDecimal.ZERO;

            // 5.5 Set discount vào order
            savedOrder.setDiscountCode(voucherCode);
            savedOrder.setDiscountPercentage(discountPercentage);
            savedOrder.setDiscountAmount(discountAmount);
            savedOrder.setFinalPrice(finalPrice);
            savedOrder.setDiscountApplied(true);

            // 5.6 Đánh dấu voucher đã dùng
            userVoucher.setStatus(VoucherStatus.USED);
            userVoucher.setUsedAt(now);
            userVoucherRepository.save(userVoucher);

        } else {
            savedOrder.setFinalPrice(subtotal);
            savedOrder.setDiscountApplied(false);
        }

        // BƯỚC 6: Cập nhật totalPrice và finalPrice, KHÔNG setDetails
        savedOrder.setTotalPrice(subtotal);
        bulkOrderRepository.save(savedOrder);

        // BƯỚC 7: Reload từ DB để lấy đầy đủ details rồi return
        BulkOrder reloaded = bulkOrderRepository.findById(savedOrder.getBulkOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found"));
        return buildFullResponse(reloaded);
    }

    @Override
    @Transactional
    public BulkOrderResponse updateStatus(Integer id, BulkOrderStatus status) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));

        bulkOrder.setStatus(status);
        BulkOrder updated = bulkOrderRepository.save(bulkOrder);
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
                .extraFee(request.getExtraFee() != null ? request.getExtraFee() : BigDecimal.ZERO)
                .build();

        orderCustomizationRepository.save(customization);

        // Recalculate total price for the parent bulk order
        BulkOrder bulkOrder = detail.getBulkOrder();
        recalculateTotalPrice(bulkOrder);
        bulkOrderRepository.save(bulkOrder);

        return buildFullResponse(bulkOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public BulkOrderResponse getPriceBreakdown(Integer bulkOrderId) {
        BulkOrder bulkOrder = bulkOrderRepository.findById(bulkOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + bulkOrderId));
        return buildFullResponse(bulkOrder);
    }

    // ======================== HELPER METHODS ========================

    private BulkOrderResponse buildFullResponse(BulkOrder bulkOrder) {
        BulkOrderResponse response = bulkOrderMapper.toResponse(bulkOrder);

        List<BulkOrderDetail> details = bulkOrder.getDetails();
        if (details == null || details.isEmpty()) {
            details = bulkOrderDetailRepository.findByBulkOrder_BulkOrderId(bulkOrder.getBulkOrderId());
        }

        List<BulkOrderDetailResponse> detailResponses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (BulkOrderDetail detail : details) {
            BulkOrderDetailResponse detailResponse = bulkOrderMapper.toDetailResponse(detail);

            // 1. Lookup applied tier price
            BigDecimal appliedTierPrice = lookupTierPrice(detail);
            detailResponse.setAppliedTierPrice(appliedTierPrice);

            // 2. Calculate customization fee
            BigDecimal customizationFee = calculateCustomizationFee(detail);
            detailResponse.setCustomizationFee(customizationFee);

            // 3. Calculate line total = quantity × appliedTierPrice + customizationFee
            BigDecimal lineTotal = appliedTierPrice
                    .multiply(BigDecimal.valueOf(detail.getQuantity()))
                    .add(customizationFee);
            detailResponse.setLineTotal(lineTotal);

            totalPrice = totalPrice.add(lineTotal);
            detailResponses.add(detailResponse);
        }

        response.setDetails(detailResponses);
        response.setTotalPrice(totalPrice);
        return response;
    }

    private BigDecimal lookupTierPrice(BulkOrderDetail detail) {
        Optional<BulkPriceTier> matchedTier = bulkPriceTierRepository
                .findTopByBulkOrderDetail_BulkOrderDetailIdAndMinQtyLessThanEqualOrderByMinQtyDesc(
                        detail.getBulkOrderDetailId(), detail.getQuantity());

        return matchedTier.map(BulkPriceTier::getUnitPrice)
                .orElse(detail.getUnitPriceSnapshot() != null ? detail.getUnitPriceSnapshot() : BigDecimal.ZERO);
    }

    private BigDecimal calculateCustomizationFee(BulkOrderDetail detail) {
        List<OrderCustomization> customizations = detail.getCustomizations();
        if (customizations == null || customizations.isEmpty()) {
            customizations = orderCustomizationRepository
                    .findByBulkOrderDetail_BulkOrderDetailId(detail.getBulkOrderDetailId());
        }

        return customizations.stream()
                .filter(c -> c.getExtraFee() != null)
                .map(OrderCustomization::getExtraFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recalculateTotalPrice(BulkOrder bulkOrder) {
        List<BulkOrderDetail> details = bulkOrderDetailRepository
                .findByBulkOrder_BulkOrderId(bulkOrder.getBulkOrderId());

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (BulkOrderDetail detail : details) {
            BigDecimal appliedTierPrice = lookupTierPrice(detail);
            BigDecimal customizationFee = calculateCustomizationFee(detail);
            BigDecimal lineTotal = appliedTierPrice
                    .multiply(BigDecimal.valueOf(detail.getQuantity()))
                    .add(customizationFee);
            totalPrice = totalPrice.add(lineTotal);
        }

        bulkOrder.setTotalPrice(totalPrice);
    }
}
