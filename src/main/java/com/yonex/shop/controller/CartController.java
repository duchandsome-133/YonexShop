package com.yonex.shop.controller;

import com.yonex.shop.config.CustomUserDetails;
import com.yonex.shop.dto.AddToCartRequest;
import com.yonex.shop.entity.Cart;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Cart cart = cartService.getOrCreateCart(userDetails.getUserId());
        model.addAttribute("cart", cart);
        return "client/cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute AddToCartRequest request,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            cartService.addToCart(userDetails.getUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng thành công!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return referer != null ? "redirect:" + referer : "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("cartItemId") Long cartItemId,
            @RequestParam("quantity") int quantity,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            cartService.updateCartItem(userDetails.getUserId(), cartItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật giỏ hàng thành công!");
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("cartItemId") Long cartItemId,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            cartService.removeCartItem(userDetails.getUserId(), cartItemId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        cartService.clearCart(userDetails.getUserId());
        redirectAttributes.addFlashAttribute("successMessage", "Đã làm rỗng giỏ hàng.");
        return "redirect:/cart";
    }
}
