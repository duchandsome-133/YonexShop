package com.yonex.shop.controller.admin;

import com.yonex.shop.config.CustomUserDetails;
import com.yonex.shop.entity.Order;
import com.yonex.shop.entity.OrderStatus;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<Order> orderPage = orderService.searchOrdersAdmin(
                keyword, status, PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("orderStatuses", OrderStatus.values());

        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "admin/order-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") OrderStatus newStatus,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderService.updateOrderStatus(id, newStatus, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Cập nhật trạng thái đơn hàng sang '" + newStatus.getDisplayName() + "' thành công!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật trạng thái đơn.");
        }
        return "redirect:/admin/orders/" + id;
    }
}
