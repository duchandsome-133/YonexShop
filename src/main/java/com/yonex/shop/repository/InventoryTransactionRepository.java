package com.yonex.shop.repository;

import com.yonex.shop.entity.InventoryTransaction;
import com.yonex.shop.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByVariantOrderByCreatedAtDesc(ProductVariant variant);
    Page<InventoryTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
