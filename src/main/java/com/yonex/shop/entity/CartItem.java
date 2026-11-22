package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CartItems", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Cart_Variant", columnNames = {"CartId", "VariantId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartItemId")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CartId", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VariantId", nullable = false)
    private ProductVariant variant;

    @Column(name = "Quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BigDecimal getUnitPrice() {
        return variant != null ? variant.getEffectivePrice() : BigDecimal.ZERO;
    }

    public BigDecimal getLineTotal() {
        return getUnitPrice().multiply(BigDecimal.valueOf(quantity != null ? quantity : 0));
    }
}
