package com.yonex.shop.service;

import com.yonex.shop.dto.AddToCartRequest;
import com.yonex.shop.entity.Cart;

public interface CartService {
    Cart getOrCreateCart(Long userId);
    void addToCart(Long userId, AddToCartRequest request);
    void updateCartItem(Long userId, Long cartItemId, int quantity);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
    int getCartItemCount(Long userId);
}
