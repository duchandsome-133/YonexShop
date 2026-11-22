package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductImages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageId")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductId", nullable = false)
    private Product product;

    @Column(name = "ImageUrl", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "IsPrimary", nullable = false)
    @Builder.Default
    private boolean isPrimary = false;

    @Column(name = "DisplayOrder", nullable = false)
    @Builder.Default
    private int displayOrder = 0;
}
