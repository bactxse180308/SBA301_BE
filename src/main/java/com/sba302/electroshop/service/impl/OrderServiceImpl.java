package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateOrderRequest;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.enums.ProductStatus;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.enums.VoucherStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.OrderMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final StoreBranchRepository storeBranchRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> search(Integer userId, OrderStatus status, Pageable pageable) {
        Page<Order> page;

        if (userId != null && status != null) {
            page = orderRepository.findAll(pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        return page.map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Integer userId, CreateOrderRequest request) {
        log.info("Placing order for userId={}", userId);

        // 1. Validate user active
        User user = findActiveUser(userId);

        // 2. Validate items — check duplicate productId
        validateNoDuplicateProducts(request.getItems());

        // 3. Batch fetch products + validate (tồn tại, status, price, stock)
        Map<Integer, Product> productMap = fetchAndValidateProducts(request.getItems());

        // 4. Build order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();
        order = orderRepository.save(order);

        // 5. Build details + deduct stock (batch)
        BigDecimal totalAmount = buildOrderDetailsAndDeductStock(order, request.getItems(), productMap);
        order.setTotalAmount(totalAmount);

        // 6. Apply voucher nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            applyVoucherDiscount(order, userId, request.getVoucherCode(), totalAmount);
        }

        // 7. Save & return
        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with id={}", savedOrder.getOrderId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Integer orderId, OrderStatus newStatus) {
        log.info("Updating order status: orderId={}, newStatus={}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {
        log.info("Cancelling order: orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse applyVoucher(Integer orderId, String voucherCode) {
        log.info("Applying voucher {} to order {}", voucherCode, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + voucherCode));

        // Validate voucher
        validateVoucherExpiry(voucher);

        BigDecimal total = order.getTotalAmount();
        BigDecimal discount = calculateDiscount(voucher, total);
        BigDecimal finalTotal = total.subtract(discount);

        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        order.setVoucher(voucher);
        order.setTotalAmount(finalTotal);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    // ======================== PRIVATE HELPER METHODS ========================

    /**
     * Validate user exists and is ACTIVE.
     */
    private User findActiveUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException("User account is not active. Current status: " + user.getStatus());
        }
        return user;
    }

    /**
     * Check for duplicate productIds in the request.
     */
    private void validateNoDuplicateProducts(List<CreateOrderRequest.OrderItemRequest> items) {
        Set<Integer> uniqueIds = new HashSet<>();
        for (var item : items) {
            if (!uniqueIds.add(item.getProductId())) {
                throw new ApiException("Duplicate product ID in order items: " + item.getProductId());
            }
        }
    }

    /**
     * Batch fetch all products in 1 query, then validate each one:
     * - Must exist
     * - Status must be AVAILABLE
     * - Price must be valid (not null, > 0)
     * - Stock must be sufficient
     */
    private Map<Integer, Product> fetchAndValidateProducts(List<CreateOrderRequest.OrderItemRequest> items) {
        List<Integer> productIds = items.stream()
                .map(CreateOrderRequest.OrderItemRequest::getProductId)
                .toList();

        // 1 query: SELECT * FROM PRODUCT WHERE product_id IN (...)
        Map<Integer, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        for (var item : items) {
            Product product = productMap.get(item.getProductId());

            // Check tồn tại
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + item.getProductId());
            }

            // Check status
            if (product.getStatus() != ProductStatus.AVAILABLE) {
                throw new ApiException("Product is not available: " + product.getProductName()
                        + " (status: " + product.getStatus() + ")");
            }

            // Check price
            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException("Product price is invalid: " + product.getProductName());
            }

            // Check stock
            if (product.getQuantity() == null || product.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient stock for product: " + product.getProductName()
                        + " (available: " + (product.getQuantity() != null ? product.getQuantity() : 0)
                        + ", requested: " + item.getQuantity() + ")");
            }
        }

        return productMap;
    }

    /**
     * Build OrderDetail list, deduct stock, resolve branch, and saveAll in batch.
     * Returns the total amount.
     */
    private BigDecimal buildOrderDetailsAndDeductStock(Order order,
                                                       List<CreateOrderRequest.OrderItemRequest> items,
                                                       Map<Integer, Product> productMap) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();
        List<Product> updatedProducts = new ArrayList<>();

        for (var item : items) {
            Product product = productMap.get(item.getProductId());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderDetail.OrderDetailBuilder detailBuilder = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal);

            // Resolve branch if provided
            if (item.getBranchId() != null) {
                StoreBranch branch = storeBranchRepository.findById(item.getBranchId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Store branch not found with id: " + item.getBranchId()));
                detailBuilder.branch(branch);

                // Also deduct branch stock
                BranchProductStock branchStock = branchProductStockRepository
                        .findByBranch_BranchIdAndProduct_ProductId(item.getBranchId(), item.getProductId())
                        .orElseThrow(() -> new ApiException(
                                "Product not available at branch: " + item.getBranchId()));

                if (branchStock.getQuantity() < item.getQuantity()) {
                    throw new ApiException("Insufficient branch stock for product: " + product.getProductName()
                            + " at branch " + item.getBranchId()
                            + " (available: " + branchStock.getQuantity()
                            + ", requested: " + item.getQuantity() + ")");
                }

                branchStock.setQuantity(branchStock.getQuantity() - item.getQuantity());
                branchStock.setLastUpdated(LocalDateTime.now());
                branchProductStockRepository.save(branchStock);
            }

            details.add(detailBuilder.build());

            // Deduct main product stock
            product.setQuantity(product.getQuantity() - item.getQuantity());
            updatedProducts.add(product);

            total = total.add(subtotal);
        }

        // Batch insert order details
        orderDetailRepository.saveAll(details);

        // Batch update product stock
        productRepository.saveAll(updatedProducts);

        return total;
    }

    /**
     * Full voucher validation and discount application:
     * - Check voucher exists
     * - Check expiry (validFrom / validTo)
     * - Check usageLimit
     * - Check UserVoucher exists and not USED
     * - Calculate discount with RoundingMode.HALF_UP
     * - Mark UserVoucher as USED
     */
    private void applyVoucherDiscount(Order order, Integer userId, String voucherCode, BigDecimal totalAmount) {
        // Find voucher
        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + voucherCode));

        // Check expiry
        validateVoucherExpiry(voucher);

        // Check usage limit
        if (voucher.getUsageLimit() != null) {
            long usedCount = orderRepository.countByVoucher_VoucherId(voucher.getVoucherId());
            if (usedCount >= voucher.getUsageLimit()) {
                throw new ApiException("Voucher usage limit reached: " + voucherCode);
            }
        }

        // Check UserVoucher
        UserVoucher userVoucher = userVoucherRepository
                .findByUser_UserIdAndVoucher_VoucherCode(userId, voucherCode)
                .orElseThrow(() -> new ApiException("User does not have this voucher: " + voucherCode));

        if (userVoucher.getStatus() == VoucherStatus.USED) {
            throw new ApiException("Voucher already used: " + voucherCode);
        }

        // Calculate discount
        BigDecimal discount = calculateDiscount(voucher, totalAmount);
        BigDecimal finalAmount = totalAmount.subtract(discount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // Apply to order
        order.setVoucher(voucher);
        order.setTotalAmount(finalAmount);

        // Mark voucher as used
        userVoucher.setStatus(VoucherStatus.USED);
        userVoucher.setUsedAt(LocalDateTime.now());
        userVoucherRepository.save(userVoucher);

        log.info("Voucher {} applied. Discount: {}, Final: {}", voucherCode, discount, finalAmount);
    }

    /**
     * Validate voucher is within valid date range.
     */
    private void validateVoucherExpiry(Voucher voucher) {
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getValidFrom() != null && now.isBefore(voucher.getValidFrom())) {
            throw new ApiException("Voucher is not yet active: " + voucher.getVoucherCode());
        }
        if (voucher.getValidTo() != null && now.isAfter(voucher.getValidTo())) {
            throw new ApiException("Voucher has expired: " + voucher.getVoucherCode());
        }
    }

    /**
     * Calculate discount amount based on discount type (PERCENTAGE or FIXED_AMOUNT).
     * Uses RoundingMode.HALF_UP to avoid ArithmeticException.
     */
    private BigDecimal calculateDiscount(Voucher voucher, BigDecimal total) {
        if ("PERCENTAGE".equalsIgnoreCase(voucher.getDiscountType())) {
            return total.multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            return voucher.getDiscountValue() != null ? voucher.getDiscountValue() : BigDecimal.ZERO;
        }
    }
}
