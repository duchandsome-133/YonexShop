package com.yonex.shop.repository;

import com.yonex.shop.entity.OrderItem;
import com.yonex.shop.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.productNameSnapshot, SUM(oi.quantity), SUM(oi.lineTotal) " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.orderStatus = :status " +
           "GROUP BY oi.productNameSnapshot " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findBestSellingProducts(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi JOIN oi.order o WHERE o.orderStatus = :status")
    long countTotalSoldItemsByStatus(@Param("status") OrderStatus status);
}
