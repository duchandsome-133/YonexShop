package com.yonex.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDTO {
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long shippingOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    private BigDecimal totalRevenue;
    private BigDecimal revenueToday;
    private BigDecimal revenueThisMonth;

    private long totalProductsSold;
    private List<BestSellingProductDTO> bestSellingProducts;
}
