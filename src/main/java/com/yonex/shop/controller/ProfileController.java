package com.yonex.shop.controller;

import com.yonex.shop.config.CustomUserDetails;
import com.yonex.shop.dto.AddressRequest;
import com.yonex.shop.dto.ChangePasswordRequest;
import com.yonex.shop.dto.ProfileUpdateRequest;
import com.yonex.shop.dto.ReviewRequest;
import com.yonex.shop.entity.Address;
import com.yonex.shop.entity.Order;
import com.yonex.shop.entity.User;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.service.OrderService;
import com.yonex.shop.service.ReviewService;
import com.yonex.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";

        User user = userService.getById(userDetails.getUserId());
        if (!model.containsAttribute("profileRequest")) {
            model.addAttribute("profileRequest", ProfileUpdateRequest.builder()
                    .fullName(user.getFullName())
                    .phone(user.getPhone())
                    .build());
        }
        if (!model.containsAttribute("passwordRequest")) {
            model.addAttribute("passwordRequest", new ChangePasswordRequest());
        }
        model.addAttribute("user", user);
        return "client/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("profileRequest") ProfileUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("profileRequest", request);
            return "redirect:/profile";
        }

        try {
            userService.updateProfile(userDetails.getUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("passwordRequest") ChangePasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("passwordRequest", request);
            return "redirect:/profile";
        }

        try {
            userService.changePassword(userDetails.getUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/profile/addresses")
    public String addressBook(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";

        List<Address> addresses = userService.getUserAddresses(userDetails.getUserId());
        model.addAttribute("addresses", addresses);
        if (!model.containsAttribute("addressRequest")) {
            model.addAttribute("addressRequest", new AddressRequest());
        }
        return "client/addresses";
    }

    @PostMapping("/profile/addresses/add")
    public String addAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("addressRequest") AddressRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addressRequest", bindingResult);
            redirectAttributes.addFlashAttribute("addressRequest", request);
            return "redirect:/profile/addresses";
        }

        userService.addAddress(userDetails.getUserId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm địa chỉ nhận hàng mới.");
        return "redirect:/profile/addresses";
    }

    @PostMapping("/profile/addresses/{id}/update")
    public String updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long addressId,
            @Valid @ModelAttribute("addressRequest") AddressRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    bindingResult.getAllErrors().isEmpty() ? "Dữ liệu không hợp lệ." : bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/profile/addresses";
        }

        try {
            userService.updateAddress(userDetails.getUserId(), addressId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật địa chỉ nhận hàng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile/addresses";
    }

    @PostMapping("/profile/addresses/{id}/default")
    public String setDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long addressId,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";
        userService.setDefaultAddress(userDetails.getUserId(), addressId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã đặt địa chỉ mặc định.");
        return "redirect:/profile/addresses";
    }

    @PostMapping("/profile/addresses/{id}/delete")
    public String deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long addressId,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";
        userService.deleteAddress(userDetails.getUserId(), addressId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa địa chỉ.");
        return "redirect:/profile/addresses";
    }

    // Customer Order Tracking
    @GetMapping("/orders")
    public String myOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        if (userDetails == null) return "redirect:/login";

        Page<Order> ordersPage = orderService.getOrdersByUser(userDetails.getUserId(), PageRequest.of(page, 10));
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        return "client/orders";
    }

    @GetMapping("/orders/{orderCode}")
    public String orderDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("orderCode") String orderCode,
            Model model
    ) {
        if (userDetails == null) return "redirect:/login";

        Order order = orderService.getOrderByCodeAndUser(orderCode, userDetails.getUserId());
        model.addAttribute("order", order);
        return "client/order-detail";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long orderId,
            @RequestParam(name = "reason", required = false) String reason,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";

        try {
            orderService.cancelOrderByCustomer(orderId, userDetails.getUserId(), reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng thành công.");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/reviews/add")
    public String submitReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute ReviewRequest request,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) return "redirect:/login";

        try {
            reviewService.addReview(userDetails.getUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã đánh giá sản phẩm!");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }
}
