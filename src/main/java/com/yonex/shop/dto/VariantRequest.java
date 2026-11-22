package com.yonex.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantRequest {

    private Long variantId;

    @NotBlank(message = "Mã SKU biến thể không được để trống")
    private String sku;

    @NotBlank(message = "Tên biến thể không được để trống")
    private String variantName;

    private String color;
    private String size;

    @Builder.Default
    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Builder.Default
    private Integer quantity = 0;
}
