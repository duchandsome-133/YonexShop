package com.yonex.shop.service.impl;

import com.yonex.shop.dto.BestSellingProductDTO;
import com.yonex.shop.dto.DashboardStatisticsDTO;
import com.yonex.shop.entity.OrderStatus;
import com.yonex.shop.repository.OrderItemRepository;
import com.yonex.shop.repository.OrderRepository;
import com.yonex.shop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsDTO getDashboardStatistics() {
        long total = orderRepository.count();
        long pending = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        long confirmed = orderRepository.countByOrderStatus(OrderStatus.CONFIRMED);
        long shipping = orderRepository.countByOrderStatus(OrderStatus.SHIPPING);
        long delivered = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
        long cancelled = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

        // Doanh thu chỉ tính đơn DELIVERED (BR-15)
        BigDecimal totalRevenue = orderRepository.sumTotalRevenueByStatus(OrderStatus.DELIVERED);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        BigDecimal revenueToday = orderRepository.sumRevenueByStatusSince(OrderStatus.DELIVERED, startOfToday);
        if (revenueToday == null) revenueToday = BigDecimal.ZERO;

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        BigDecimal revenueThisMonth = orderRepository.sumRevenueByStatusSince(OrderStatus.DELIVERED, startOfMonth);
        if (revenueThisMonth == null) revenueThisMonth = BigDecimal.ZERO;

        long totalSold = orderItemRepository.countTotalSoldItemsByStatus(OrderStatus.DELIVERED);

        List<Object[]> rawBestSellers = orderItemRepository.findBestSellingProducts(OrderStatus.DELIVERED, PageRequest.of(0, 5));
        List<BestSellingProductDTO> bestSellers = new ArrayList<>();
        for (Object[] row : rawBestSellers) {
            bestSellers.add(BestSellingProductDTO.builder()
                    .productName((String) row[0])
                    .totalQuantitySold(((Number) row[1]).longValue())
                    .totalRevenue((BigDecimal) row[2])
                    .build());
        }

        return DashboardStatisticsDTO.builder()
                .totalOrders(total)
                .pendingOrders(pending)
                .confirmedOrders(confirmed)
                .shippingOrders(shipping)
                .deliveredOrders(delivered)
                .cancelledOrders(cancelled)
                .totalRevenue(totalRevenue)
                .revenueToday(revenueToday)
                .revenueThisMonth(revenueThisMonth)
                .totalProductsSold(totalSold)
                .bestSellingProducts(bestSellers)
                .build();
    }
}
