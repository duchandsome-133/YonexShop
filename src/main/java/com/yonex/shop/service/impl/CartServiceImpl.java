package com.yonex.shop.service.impl;

import com.yonex.shop.dto.AddToCartRequest;
import com.yonex.shop.entity.*;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.exception.ResourceNotFoundException;
import com.yonex.shop.repository.CartItemRepository;
import com.yonex.shop.repository.CartRepository;
import com.yonex.shop.repository.ProductVariantRepository;
import com.yonex.shop.repository.UserRepository;
import com.yonex.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public void addToCart(Long userId, AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Số lượng đặt mua phải là số nguyên dương");
        }

        Cart cart = getOrCreateCart(userId);
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Biến thể sản phẩm không tồn tại"));

        if (!variant.isActive() || !variant.getProduct().isActive()) {
            throw new BadRequestException("Sản phẩm hiện đang tạm ngừng kinh doanh");
        }

        int available = variant.getAvailableQuantity();
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndVariant(cart, variant);

        int newTotalQuantity = request.getQuantity();
        if (existingItemOpt.isPresent()) {
            newTotalQuantity += existingItemOpt.get().getQuantity();
        }

        if (newTotalQuantity > available) {
            throw new BadRequestException("Số lượng yêu cầu (" + newTotalQuantity + ") vượt quá tồn kho khả dụng (" + available + ")");
        }

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(newTotalQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void updateCartItem(Long userId, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ"));

        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Bạn không có quyền sửa giỏ hàng này");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return;
        }

        int available = item.getVariant().getAvailableQuantity();
        if (quantity > available) {
            throw new BadRequestException("Số lượng yêu cầu (" + quantity + ") vượt quá tồn kho khả dụng (" + available + ")");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng giỏ hàng"));

        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Không có quyền thao tác trên giỏ hàng này");
        }

        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        return cartRepository.findByUser_UserId(userId)
                .map(Cart::getTotalQuantity)
                .orElse(0);
    }
}
