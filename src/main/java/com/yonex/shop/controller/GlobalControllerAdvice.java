package com.yonex.shop.controller;

import com.yonex.shop.config.CustomUserDetails;
import com.yonex.shop.entity.Category;
import com.yonex.shop.entity.User;
import com.yonex.shop.service.CartService;
import com.yonex.shop.service.CategoryService;
import com.yonex.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CategoryService categoryService;
    private final CartService cartService;
    private final UserService userService;

    @ModelAttribute("activeCategories")
    public List<Category> getActiveCategories() {
        try {
            return categoryService.getAllActive();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @ModelAttribute("cartCount")
    public int getCartCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            try {
                return cartService.getCartItemCount(userDetails.getUserId());
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            try {
                return userService.getById(userDetails.getUserId());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
