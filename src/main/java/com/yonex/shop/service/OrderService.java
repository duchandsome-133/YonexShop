package com.yonex.shop.service;

import com.yonex.shop.dto.CheckoutRequest;
import com.yonex.shop.entity.Order;
import com.yonex.shop.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrderFromCart(Long userId, CheckoutRequest request);
    Order getOrderByIdAndUser(Long orderId, Long userId);
    Order getOrderByCodeAndUser(String orderCode, Long userId);
    Page<Order> getOrdersByUser(Long userId, Pageable pageable);
    void cancelOrderByCustomer(Long orderId, Long userId, String reason);

    // Admin / Staff
    Page<Order> searchOrdersAdmin(String keyword, OrderStatus status, Pageable pageable);
    Order getOrderById(Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus newStatus, String adminUsername);
}
