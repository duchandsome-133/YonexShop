package com.yonex.shop.service;

import com.yonex.shop.dto.CategoryRequest;
import com.yonex.shop.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllActive();
    List<Category> getAll();
    Category getById(Long id);
    Category getBySlug(String slug);
    Category create(CategoryRequest request);
    Category update(Long id, CategoryRequest request);
    void delete(Long id);
    void toggleActive(Long id);
}
