package com.yonex.shop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long productId;

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private Long categoryId;

    @NotBlank(message = "Mã SKU không được để trống")
    @Size(max = 50, message = "Mã SKU tối đa 50 ký tự")
    private String sku;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String name;

    private String description;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", message = "Giá bán không được âm")
    private BigDecimal price;

    @Builder.Default
    private boolean active = true;

    private String imageUrl;

    @Builder.Default
    private Integer initialStock = 10;

    @Builder.Default
    private List<VariantRequest> variants = new ArrayList<>();
}
