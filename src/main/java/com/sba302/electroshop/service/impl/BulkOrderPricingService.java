package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.BulkOrder;
import com.sba302.electroshop.entity.BulkOrderDetail;
import com.sba302.electroshop.entity.BulkPriceTier;
import com.sba302.electroshop.entity.OrderCustomization;
import com.sba302.electroshop.enums.CustomizationStatus;
import com.sba302.electroshop.repository.BulkPriceTierRepository;
import com.sba302.electroshop.repository.OrderCustomizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkOrderPricingService {

    private final BulkPriceTierRepository bulkPriceTierRepository;
    private final OrderCustomizationRepository orderCustomizationRepository;

    /**
     * Lookup tier price for product + quantity.
     * Uses minQty desc to find the largest tier <= quantity.
     */
    public BigDecimal lookupTierPrice(Integer productId, Integer quantity, BigDecimal defaultPrice) {
        return bulkPriceTierRepository
                .findTopByProduct_ProductIdAndMinQtyLessThanEqualAndIsActiveTrueOrderByMinQtyDesc(
                        productId, quantity)
                .map(BulkPriceTier::getUnitPrice)
                .orElse(defaultPrice != null ? defaultPrice : BigDecimal.ZERO);
    }

    /**
     * Calculate customization fee for one detail (Confirmed/Approved only).
     */
    public BigDecimal calculateCustomizationFee(BulkOrderDetail detail) {
        return calculateCustomizationFeeByStatus(detail, CustomizationStatus.APPROVED);
    }

    /**
     * Calculate customization fee for one detail with a specific status.
     */
    public BigDecimal calculateCustomizationFeeByStatus(BulkOrderDetail detail, CustomizationStatus status) {
        List<OrderCustomization> customizations = getCustomizations(detail);
        return customizations.stream()
                .filter(c -> c.getExtraFee() != null && c.getStatus() == status)
                .map(c -> calculateCustomizationTotalFee(c, detail.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateCustomizationTotalFee(OrderCustomization c, Integer quantity) {
        if (c.getExtraFee() == null) return BigDecimal.ZERO;
        return "FIXED".equals(c.getFeeType())
                ? c.getExtraFee()
                : c.getExtraFee().multiply(BigDecimal.valueOf(quantity));
    }

    private List<OrderCustomization> getCustomizations(BulkOrderDetail detail) {
        List<OrderCustomization> customizations = detail.getCustomizations();
        if (customizations == null || customizations.isEmpty()) {
            customizations = orderCustomizationRepository
                    .findByBulkOrderDetail_BulkOrderDetailId(detail.getBulkOrderDetailId());
        }
        return customizations;
    }

    public String buildTierLabel(BulkOrderDetail detail, BigDecimal appliedTierPrice) {
        // Find the tier to get the label or discount percent
        return bulkPriceTierRepository
                .findTopByProduct_ProductIdAndMinQtyLessThanEqualAndIsActiveTrueOrderByMinQtyDesc(
                        detail.getProduct().getProductId(), detail.getQuantity())
                .map(tier -> {
                    String range = tier.getMinQty() + (tier.getMaxQty() != null ? "-" + tier.getMaxQty() : "+");
                    BigDecimal basePrice = detail.getProduct().getPrice();
                    if (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal discount = basePrice.subtract(appliedTierPrice)
                                .divide(basePrice, 4, java.math.RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                        return String.format("Bậc %s sp (-%.0f%%)", range, discount);
                    }
                    return "Bậc " + range;
                })
                .orElse("Giá thỏa thuận");
    }

    /**
     * Calculate line total for one detail. 
     * Applies tier price * quantity + customization fee.
     */
    public BigDecimal calculateLineTotal(BulkOrderDetail detail) {
        BigDecimal tierPrice = detail.getAppliedTierPrice() != null
                ? detail.getAppliedTierPrice()
                : lookupTierPrice(
                    detail.getProduct().getProductId(),
                    detail.getQuantity(),
                    detail.getProduct().getPrice());

        detail.setAppliedTierPrice(tierPrice);

        return tierPrice
                .multiply(BigDecimal.valueOf(detail.getQuantity()))
                .add(calculateCustomizationFee(detail));
    }

    /**
     * Recalculate subtotal + finalPrice for the entire order.
     */
    public void recalculate(BulkOrder order, List<BulkOrderDetail> details) {
        BigDecimal subtotal = details.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSubtotalAfterTier(subtotal);

        BigDecimal discount = Boolean.TRUE.equals(order.getDiscountApplied())
                && order.getVoucherDiscountAmount() != null
                ? order.getVoucherDiscountAmount()
                : BigDecimal.ZERO;

        BigDecimal shippingFee = Boolean.TRUE.equals(order.getShippingFeeWaived())
                || order.getShippingFee() == null
                ? BigDecimal.ZERO
                : order.getShippingFee();

        order.setFinalPrice(subtotal
                .subtract(discount)
                .add(shippingFee)
                .max(BigDecimal.ZERO));

        order.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
