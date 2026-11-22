package com.yonex.shop.repository;

import com.yonex.shop.entity.Product;
import com.yonex.shop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductOrderByDisplayOrderAsc(Product product);
}
