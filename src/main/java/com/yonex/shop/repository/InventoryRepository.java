package com.yonex.shop.repository;

import com.yonex.shop.entity.Inventory;
import com.yonex.shop.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByVariant(ProductVariant variant);

    Optional<Inventory> findByVariant_VariantId(Long variantId);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :qty, i.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE i.variant.variantId = :variantId AND i.quantity >= :qty")
    int deductStockConditional(@Param("variantId") Long variantId, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :qty, i.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE i.variant.variantId = :variantId")
    int restoreStock(@Param("variantId") Long variantId, @Param("qty") int qty);
}
