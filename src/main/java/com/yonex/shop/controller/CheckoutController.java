package com.yonex.shop.controller;

import com.yonex.shop.config.CustomUserDetails;
import com.yonex.shop.dto.CheckoutRequest;
import com.yonex.shop.entity.Address;
import com.yonex.shop.entity.Cart;
import com.yonex.shop.entity.Order;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.service.CartService;
import com.yonex.shop.service.OrderService;
import com.yonex.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    private static final BigDecimal FIXED_SHIPPING_FEE = new BigDecimal("30000.00");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("1000000.00");

    @GetMapping
    public String checkoutPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getOrCreateCart(userDetails.getUserId());
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        List<Address> addresses = userService.getUserAddresses(userDetails.getUserId());
        BigDecimal subtotal = cart.getTotalAmount();
        BigDecimal shippingFee = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : FIXED_SHIPPING_FEE;
        BigDecimal totalAmount = subtotal.add(shippingFee);

        if (!model.containsAttribute("checkoutRequest")) {
            CheckoutRequest defaultRequest = new CheckoutRequest();
            if (!addresses.isEmpty()) {
                Address defaultAddr = addresses.stream()
                        .filter(Address::isDefault)
                        .findFirst()
                        .orElse(addresses.get(0));
                defaultRequest.setSelectedAddressId(defaultAddr.getAddressId());
                defaultRequest.setRecipientName(defaultAddr.getRecipientName());
                defaultRequest.setRecipientPhone(defaultAddr.getPhone());
                defaultRequest.setShippingAddress(defaultAddr.getFullAddress());
            } else {
                defaultRequest.setRecipientName(userDetails.getFullName());
            }
            model.addAttribute("checkoutRequest", defaultRequest);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("addresses", addresses);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("totalAmount", totalAmount);

        return "client/checkout";
    }

    @PostMapping
    public String placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("checkoutRequest") CheckoutRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return checkoutPage(userDetails, model);
        }

        try {
            Order order = orderService.createOrderFromCart(userDetails.getUserId(), request);
            return "redirect:/checkout/success/" + order.getOrderCode();
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/checkout";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi tạo đơn hàng. Vui lòng thử lại.");
            return "redirect:/checkout";
        }
    }

    @GetMapping("/success/{orderCode}")
    public String orderSuccess(
            @PathVariable("orderCode") String orderCode,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderByCodeAndUser(orderCode, userDetails.getUserId());
        model.addAttribute("order", order);
        return "client/order-success";
    }
}
