package com.yonex.shop.service.impl;

import com.yonex.shop.dto.ProductRequest;
import com.yonex.shop.dto.VariantRequest;
import com.yonex.shop.entity.*;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.exception.ResourceNotFoundException;
import com.yonex.shop.repository.*;
import com.yonex.shop.service.CategoryService;
import com.yonex.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductsClient(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else if ("newest".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return productRepository.searchProductsClient(cleanKeyword, categoryId, minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductsAdmin(String keyword, Long categoryId, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return productRepository.searchProductsAdmin(cleanKeyword, categoryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findTop8ByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với SKU: " + sku));
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequest request, List<MultipartFile> imageFiles) {
        if (productRepository.existsBySku(request.getSku().trim())) {
            throw new BadRequestException("Mã SKU sản phẩm đã tồn tại: " + request.getSku());
        }

        Category category = categoryService.getById(request.getCategoryId());

        Product product = Product.builder()
                .category(category)
                .sku(request.getSku().trim())
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .isActive(request.isActive())
                .build();

        Product savedProduct = productRepository.save(product);

        // Xử lý biến thể & tồn kho
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (VariantRequest vr : request.getVariants()) {
                if (productVariantRepository.existsBySku(vr.getSku().trim())) {
                    throw new BadRequestException("Mã SKU biến thể đã tồn tại: " + vr.getSku());
                }
                ProductVariant variant = ProductVariant.builder()
                        .product(savedProduct)
                        .sku(vr.getSku().trim())
                        .variantName(vr.getVariantName().trim())
                        .color(vr.getColor())
                        .size(vr.getSize())
                        .priceAdjustment(vr.getPriceAdjustment() != null ? vr.getPriceAdjustment() : BigDecimal.ZERO)
                        .isActive(true)
                        .build();

                ProductVariant savedVariant = productVariantRepository.save(variant);

                Inventory inventory = Inventory.builder()
                        .variant(savedVariant)
                        .quantity(vr.getQuantity() != null ? vr.getQuantity() : 0)
                        .updatedAt(LocalDateTime.now())
                        .build();
                inventoryRepository.save(inventory);
            }
        } else {
            // Tạo biến thể tiêu chuẩn mặc định
            ProductVariant defaultVariant = ProductVariant.builder()
                    .product(savedProduct)
                    .sku(savedProduct.getSku() + "-STD")
                    .variantName(savedProduct.getName() + " (Tiêu chuẩn)")
                    .priceAdjustment(BigDecimal.ZERO)
                    .isActive(true)
                    .build();

            ProductVariant savedVariant = productVariantRepository.save(defaultVariant);

            Inventory inventory = Inventory.builder()
                    .variant(savedVariant)
                    .quantity(request.getInitialStock() != null ? request.getInitialStock() : 10)
                    .updatedAt(LocalDateTime.now())
                    .build();
            inventoryRepository.save(inventory);
        }

        // Xử lý ảnh tải lên
        saveProductImages(savedProduct, imageFiles, request.getImageUrl());

        return savedProduct;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest request, List<MultipartFile> imageFiles) {
        Product product = getById(id);

        if (!product.getSku().equalsIgnoreCase(request.getSku().trim()) 
                && productRepository.existsBySku(request.getSku().trim())) {
            throw new BadRequestException("Mã SKU đã tồn tại: " + request.getSku());
        }

        Category category = categoryService.getById(request.getCategoryId());

        product.setCategory(category);
        product.setSku(request.getSku().trim());
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setActive(request.isActive());

        Product savedProduct = productRepository.save(product);

        // Cập nhật hoặc thêm ảnh mới nếu có
        saveProductImages(savedProduct, imageFiles, request.getImageUrl());

        return savedProduct;
    }

    @Override
    @Transactional
    public void toggleProductStatus(Long id) {
        Product product = getById(id);
        product.setActive(!product.isActive());
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getById(id);
        // FR-08: Sản phẩm đã xuất hiện trong đơn hàng nên được ẩn/ngừng kinh doanh thay vì xóa cứng
        product.setActive(false);
        productRepository.save(product);
    }

    private void saveProductImages(Product product, List<MultipartFile> files, String directUrl) {
        if (directUrl != null && !directUrl.isBlank()) {
            boolean hasPrimary = !product.getImages().isEmpty();
            ProductImage img = ProductImage.builder()
                    .product(product)
                    .imageUrl(directUrl.trim())
                    .isPrimary(!hasPrimary)
                    .displayOrder(product.getImages().size() + 1)
                    .build();
            productImageRepository.save(img);
        }

        if (files == null || files.isEmpty()) return;

        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
            return;
        }

        int order = product.getImages() != null ? product.getImages().size() : 0;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = "";
            int dotIdx = originalName.lastIndexOf(".");
            if (dotIdx > 0) {
                extension = originalName.substring(dotIdx).toLowerCase();
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new BadRequestException("Chỉ chấp nhận các định dạng ảnh: JPG, JPEG, PNG, WEBP");
            }

            String savedFileName = UUID.randomUUID().toString() + extension;
            Path targetFile = uploadPath.resolve(savedFileName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
                order++;

                ProductImage img = ProductImage.builder()
                        .product(product)
                        .imageUrl("/uploads/" + savedFileName)
                        .isPrimary(order == 1 && product.getImages().isEmpty())
                        .displayOrder(order)
                        .build();
                productImageRepository.save(img);
            } catch (IOException e) {
                log.error("Error saving uploaded image", e);
            }
        }
    }
}
