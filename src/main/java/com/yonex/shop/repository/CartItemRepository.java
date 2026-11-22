package com.yonex.shop.repository;

import com.yonex.shop.entity.Cart;
import com.yonex.shop.entity.CartItem;
import com.yonex.shop.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndVariant(Cart cart, ProductVariant variant);
    void deleteByCart(Cart cart);
}
