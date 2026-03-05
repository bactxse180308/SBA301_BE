package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateOrderRequest;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.entity.OrderDetail;
import com.sba302.electroshop.entity.Voucher;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.OrderMapper;
import com.sba302.electroshop.repository.OrderDetailRepository;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.repository.VoucherRepository;
import com.sba302.electroshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse getById(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return orderMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> search(Integer userId, OrderStatus status, Pageable pageable) {

        Page<Order> page;

        if (userId != null && status != null) {
            page = orderRepository.findAll(pageable)
                    .map(o -> o);
        } else {
            page = orderRepository.findAll(pageable);
        }

        return page.map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Integer userId, CreateOrderRequest request) {

        Order order = new Order();

        order.setUser(userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")));
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());

        BigDecimal total = BigDecimal.ZERO;

        order = orderRepository.save(order);

        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {

            var product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found: " + item.getProductId()));

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(price);
            detail.setSubtotal(subtotal);

            orderDetailRepository.save(detail);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        if (request.getVoucherCode() != null) {

            Voucher voucher = voucherRepository
                    .findByVoucherCode(request.getVoucherCode())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Voucher not found"));

            // kiểm tra voucher còn hạn
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(voucher.getValidFrom()) || now.isAfter(voucher.getValidTo())) {
                throw new ApiException("Voucher expired or not active");
            }

            order.setVoucher(voucher);

            if ("PERCENTAGE".equals(voucher.getDiscountType())) {

                BigDecimal discount = total.multiply(voucher.getDiscountValue())
                        .divide(BigDecimal.valueOf(100));

                order.setTotalAmount(total.subtract(discount));

            } else {

                BigDecimal finalTotal = total.subtract(voucher.getDiscountValue());

                if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                    finalTotal = BigDecimal.ZERO;
                }

                order.setTotalAmount(finalTotal);
            }
        }

        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Integer orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        order.setOrderStatus(newStatus);

        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        order.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse applyVoucher(Integer orderId, String voucherCode) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        Voucher voucher = voucherRepository
                .findByVoucherCode(voucherCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Voucher not found"));

        BigDecimal total = order.getTotalAmount();

        if ("PERCENTAGE".equals(voucher.getDiscountType())) {

            BigDecimal discount = total.multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));

            total = total.subtract(discount);

        } else {

            total = total.subtract(voucher.getDiscountValue());
        }

        order.setVoucher(voucher);
        order.setTotalAmount(total);

        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }
}
