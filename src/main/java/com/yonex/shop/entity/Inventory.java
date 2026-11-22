package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InventoryId")
    private Long inventoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VariantId", nullable = false, unique = true)
    private ProductVariant variant;

    @Column(name = "Quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @UpdateTimestamp
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
