package com.yonex.shop.repository;

import com.yonex.shop.entity.Order;
import com.yonex.shop.entity.OrderStatus;
import com.yonex.shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<Order> findByOrderIdAndUser(Long orderId, User user);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.orderStatus = :status) " +
           "AND (:keyword IS NULL OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(o.recipientName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(o.recipientPhone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> searchOrdersAdmin(
            @Param("keyword") String keyword,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    long countByOrderStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderStatus = :status")
    BigDecimal sumTotalRevenueByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderStatus = :status AND o.createdAt >= :startDate")
    BigDecimal sumRevenueByStatusSince(@Param("status") OrderStatus status, @Param("startDate") LocalDateTime startDate);

    List<Order> findTop5ByOrderByCreatedAtDesc();
}
