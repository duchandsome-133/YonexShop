package com.yonex.shop.controller.admin;

import com.yonex.shop.dto.DashboardStatisticsDTO;
import com.yonex.shop.entity.Order;
import com.yonex.shop.repository.OrderRepository;
import com.yonex.shop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ReportService reportService;
    private final OrderRepository orderRepository;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        DashboardStatisticsDTO stats = reportService.getDashboardStatistics();
        List<Order> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc();

        model.addAttribute("stats", stats);
        model.addAttribute("recentOrders", recentOrders);
        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        DashboardStatisticsDTO stats = reportService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        return "admin/reports";
    }
}
