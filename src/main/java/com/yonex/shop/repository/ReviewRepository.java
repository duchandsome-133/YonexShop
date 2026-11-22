package com.yonex.shop.repository;

import com.yonex.shop.entity.Order;
import com.yonex.shop.entity.Product;
import com.yonex.shop.entity.Review;
import com.yonex.shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductAndIsVisibleTrueOrderByCreatedAtDesc(Product product);

    Page<Review> findByProductAndIsVisibleTrueOrderByCreatedAtDesc(Product product, Pageable pageable);

    boolean existsByUserAndProductAndOrder(User user, Product product, Order order);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.product = :product AND r.isVisible = true")
    Double getAverageRatingForProduct(@Param("product") Product product);

    long countByProductAndIsVisibleTrue(Product product);
}
