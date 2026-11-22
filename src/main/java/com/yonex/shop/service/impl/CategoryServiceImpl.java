package com.yonex.shop.service.impl;

import com.yonex.shop.dto.CategoryRequest;
import com.yonex.shop.entity.Category;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.exception.ResourceNotFoundException;
import com.yonex.shop.repository.CategoryRepository;
import com.yonex.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActive() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với slug: " + slug));
    }

    @Override
    @Transactional
    public Category create(CategoryRequest request) {
        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? toSlug(request.getSlug())
                : toSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription() != null ? request.getDescription().trim() : "")
                .isActive(request.isActive())
                .build();

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Long id, CategoryRequest request) {
        Category category = getById(id);

        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? toSlug(request.getSlug())
                : toSlug(request.getName());

        if (!slug.equals(category.getSlug()) && categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        category.setName(request.getName().trim());
        category.setSlug(slug);
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        category.setActive(request.isActive());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        // FR-07: Không xóa cứng danh mục đang được sản phẩm tham chiếu
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            category.setActive(false);
            categoryRepository.save(category);
        } else {
            categoryRepository.delete(category);
        }
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Category category = getById(id);
        category.setActive(!category.isActive());
        categoryRepository.save(category);
    }
}
