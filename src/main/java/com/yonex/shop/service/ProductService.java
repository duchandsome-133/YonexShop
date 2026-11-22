package com.yonex.shop.service;

import com.yonex.shop.dto.ProductRequest;
import com.yonex.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Page<Product> searchProductsClient(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, int page, int size);
    Page<Product> searchProductsAdmin(String keyword, Long categoryId, Pageable pageable);
    List<Product> getFeaturedProducts();
    Product getById(Long id);
    Product getBySku(String sku);
    Product createProduct(ProductRequest request, List<MultipartFile> imageFiles);
    Product updateProduct(Long id, ProductRequest request, List<MultipartFile> imageFiles);
    void toggleProductStatus(Long id);
    void deleteProduct(Long id);
}
