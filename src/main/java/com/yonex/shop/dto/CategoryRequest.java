package com.yonex.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    private Long categoryId;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 150, message = "Tên danh mục không vượt quá 150 ký tự")
    private String name;

    private String slug;

    @Size(max = 500, message = "Mô tả không vượt quá 500 ký tự")
    private String description;

    @Builder.Default
    private boolean active = true;
}
