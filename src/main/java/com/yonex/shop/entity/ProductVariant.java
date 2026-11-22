package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductVariants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VariantId")
    private Long variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductId", nullable = false)
    private Product product;

    @Column(name = "SKU", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "VariantName", nullable = false, length = 150)
    private String variantName;

    @Column(name = "Color", length = 50)
    private String color;

    @Column(name = "Size", length = 50)
    private String size;

    @Column(name = "PriceAdjustment", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    @Column(name = "IsActive", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Inventory inventory;

    public BigDecimal getEffectivePrice() {
        if (product == null || product.getPrice() == null) {
            return priceAdjustment != null ? priceAdjustment : BigDecimal.ZERO;
        }
        return product.getPrice().add(priceAdjustment != null ? priceAdjustment : BigDecimal.ZERO);
    }

    public int getAvailableQuantity() {
        return (inventory != null && inventory.getQuantity() != null) ? inventory.getQuantity() : 0;
    }
}
