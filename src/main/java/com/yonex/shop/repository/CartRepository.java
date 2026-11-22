package com.yonex.shop.repository;

import com.yonex.shop.entity.Cart;
import com.yonex.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUser_UserId(Long userId);
}
