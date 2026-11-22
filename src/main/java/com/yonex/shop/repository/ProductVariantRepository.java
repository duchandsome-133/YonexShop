package com.yonex.shop.repository;

import com.yonex.shop.entity.Product;
import com.yonex.shop.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findBySku(String sku);
    boolean existsBySku(String sku);
    List<ProductVariant> findByProductAndIsActiveTrue(Product product);
    List<ProductVariant> findByProduct(Product product);
}
