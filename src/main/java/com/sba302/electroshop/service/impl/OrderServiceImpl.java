package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateOrderRequest;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.entity.OrderDetail;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.enums.PaymentStatus;
import com.sba302.electroshop.enums.ProductStatus;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.OrderMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.CustomerWarrantyService;
import com.sba302.electroshop.service.EmailService;
import com.sba302.electroshop.service.OrderService;
import com.sba302.electroshop.service.StockTransactionService;
import com.sba302.electroshop.service.StoreBranchService;
import com.sba302.electroshop.service.VoucherService;
import com.sba302.electroshop.service.ShoppingCartService;
import com.sba302.electroshop.dto.response.VoucherApplicationResult;
import com.sba302.electroshop.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final OrderMapper orderMapper;
    private final VoucherService voucherService;
    private final EmailService emailService;
    private final CustomerWarrantyService customerWarrantyService;
    private final StoreBranchService storeBranchService;
    private final StockTransactionService stockTransactionService;
    private final ShoppingCartService shoppingCartService;

    // ================================================================
    // PUBLIC METHODS
    // ================================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        // Fetch order details (products) kèm theo
        List<OrderDetail> details = orderDetailRepository.findByOrderId(id);
        order.setOrderDetails(details);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> search(Integer userId, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = OrderSpecification.filterOrders(userId, status);
        Page<Order> page = orderRepository.findAll(spec, pageable);
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

        // 3. Batch fetch products, branches + validate (tồn tại, status, price, stock)
        OrderValidationContext validationContext = fetchAndValidateProducts(request.getItems());

        // 4. Build order (chưa save — chưa có totalAmount)
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        // ✅ FIX: save 1 lần để lấy orderId cho FK trong OrderDetail
        order = orderRepository.save(order);

        // 5. Build details + deduct stock (batch) → trả về totalAmount gốc
        BigDecimal totalAmount = buildOrderDetailsAndDeductStock(order, request.getItems(), validationContext);

        // 5b. Fetch saved details để ghi RESERVED transaction
        List<OrderDetail> savedDetails = orderDetailRepository.findByOrderId(order.getOrderId());
        stockTransactionService.recordReserved(order.getOrderId(), savedDetails);

        // 6. Apply voucher nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            VoucherApplicationResult voucherResult = voucherService.applyVoucher(
                    request.getVoucherCode(), userId, totalAmount);

            BigDecimal discount = voucherResult.getDiscountAmount();
            BigDecimal finalAmount = totalAmount.subtract(discount).max(BigDecimal.ZERO);

            order.setTotalAmount(totalAmount);
            order.setDiscountAmount(discount);
            order.setFinalAmount(finalAmount);
            order.setUserVoucher(voucherResult.getUserVoucher());

            voucherService.markVoucherAsUsed(voucherResult.getUserVoucher().getUserVoucherId());
            Order savedOrder = orderRepository.save(order);
            
            for (var item : request.getItems()) {
                try {
                    shoppingCartService.removeItem(userId, item.getProductId());
                } catch (Exception e) {
                    log.warn("Failed to remove product {} from cart for user {}", item.getProductId(), userId, e);
                }
            }

            log.info("Order placed with voucher, id={}, discount={}", savedOrder.getOrderId(), discount);
            return orderMapper.toResponse(savedOrder);
        }

        // 7. Không có voucher — save thẳng
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        for (var item : request.getItems()) {
            try {
                shoppingCartService.removeItem(userId, item.getProductId());
            } catch (Exception e) {
                log.warn("Failed to remove product {} from cart for user {}", item.getProductId(), userId, e);
            }
        }

        log.info("Order placed successfully with id={}", savedOrder.getOrderId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Integer orderId, OrderStatus newStatus) {
        log.info("Updating order status: orderId={}, newStatus={}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getOrderStatus().isValidTransition(newStatus)) {
            log.warn("Invalid status transition attempt from {} to {} for orderId={}", order.getOrderStatus(), newStatus, orderId);
            throw new com.sba302.electroshop.exception.InvalidStatusTransitionException(
                    "Invalid status transition from " + order.getOrderStatus() + " to " + newStatus);
        }

        order.setOrderStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            order.setUpdatedBy(auth.getName());
        } else {
            order.setUpdatedBy("SYSTEM");
        }

        Order savedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.CONFIRMED) {
            // Fetch details for email
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            savedOrder.setOrderDetails(details);
            initializeLazyFieldsForEmail(savedOrder);
            emailService.sendOrderConfirmationEmail(savedOrder);
        }

        if (newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.DELIVERED) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            savedOrder.setOrderDetails(details);
            initializeLazyFieldsForEmail(savedOrder);
            emailService.sendOrderStatusEmail(savedOrder, newStatus);
        }

        // Tự động tạo CustomerWarranty khi đơn hàng giao thành công
        if (newStatus == OrderStatus.DELIVERED) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
            savedOrder.setOrderDetails(details);
            customerWarrantyService.createFromOrder(savedOrder);
        }

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, String reason) {
        log.info("Cancelling order: orderId={}, reason={}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // 1. Validate status cancellable
        if (order.getOrderStatus() == OrderStatus.CANCELLED ||
                order.getOrderStatus() == OrderStatus.DELIVERED ||
                order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new ApiException("Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        // 2. Record cancellation (smart: RELEASED or IMPORT per branch) + restore stock
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        stockTransactionService.recordCancellation(orderId, details);

        // 3. Refund voucher if any
        if (order.getUserVoucher() != null) {
            voucherService.releaseVoucher(order.getUserVoucher().getUserVoucherId());
        }

        // 4. Update payment status if paid
        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        orderRepository.save(order);
        
        initializeLazyFieldsForEmail(order);
        emailService.sendOrderCancellationEmail(order, reason);
        
        log.info("Order cancelled and resources restored: id={}", orderId);
    }

    // ================================================================
    // PRIVATE HELPER METHODS
    // ================================================================

    private void initializeLazyFieldsForEmail(Order order) {
        if (order.getUser() != null) {
            order.getUser().getEmail();
        }
        if (order.getOrderDetails() != null) {
            order.getOrderDetails().size();
            for (OrderDetail detail : order.getOrderDetails()) {
                if (detail.getProduct() != null) {
                    detail.getProduct().getProductName();
                    detail.getProduct().getMainImage();
                }
            }
        }
        if (order.getUserVoucher() != null && order.getUserVoucher().getVoucher() != null) {
            order.getUserVoucher().getVoucher().getVoucherCode();
        }
    }

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

    private record OrderValidationContext(
            Map<Integer, Product> productMap,
            Map<Integer, BranchProductStock> selectedStockMap // productId -> selected BranchProductStock
    ) {}

    /**
     * Batch fetch all products and their available stocks, then for each item find a branch with sufficient stock.
     */
    private OrderValidationContext fetchAndValidateProducts(List<CreateOrderRequest.OrderItemRequest> items) {
        List<Integer> productIds = items.stream()
                .map(CreateOrderRequest.OrderItemRequest::getProductId)
                .distinct()
                .toList();

        // 1 query for Products
        Map<Integer, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        for (var item : items) {
            Product product = productMap.get(item.getProductId());
            validateProductBasics(product, item.getProductId());
        }

        // 1 query for ALL available stocks for these products
        List<StoreBranchService.AllocationRequest> allocationRequests = items.stream()
                .map(item -> new StoreBranchService.AllocationRequest(item.getProductId(), item.getQuantity()))
                .toList();

        StoreBranchService.AllocationResult allocationResult = storeBranchService.calculateBestAllocation(allocationRequests);
        Map<Integer, BranchProductStock> selectedStockMap = allocationResult.selectedStockMap();

        return new OrderValidationContext(productMap, selectedStockMap);
    }

    private void validateProductBasics(Product product, Integer productId) {
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new ApiException("Product is not available: " + product.getProductName()
                    + " (status: " + product.getStatus() + ")");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Product price is invalid: " + product.getProductName());
        }
    }

    /**
     * Build OrderDetail list, deduct stock via StoreBranchService, and saveAll in batch.
     * Returns the original total amount before any discount.
     */
    private BigDecimal buildOrderDetailsAndDeductStock(Order order,
                                                       List<CreateOrderRequest.OrderItemRequest> items,
                                                       OrderValidationContext validationContext) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();

        for (var item : items) {
            Product product = validationContext.productMap().get(item.getProductId());
            BranchProductStock selectedStock = validationContext.selectedStockMap().get(item.getProductId());

            // Deduct exact stock determined by greedy allocator
            BranchProductStock branchStock = storeBranchService.deductExactStock(
                    selectedStock.getBranch().getBranchId(),
                    item.getProductId(),
                    item.getQuantity()
            );

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .branch(branchStock.getBranch())
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            details.add(detail);
            total = total.add(subtotal);
        }

        orderDetailRepository.saveAll(details);
        return total;
    }

}