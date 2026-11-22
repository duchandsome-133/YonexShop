package com.yonex.shop.controller;

import com.yonex.shop.entity.Category;
import com.yonex.shop.entity.Product;
import com.yonex.shop.entity.Review;
import com.yonex.shop.service.CategoryService;
import com.yonex.shop.service.ProductService;
import com.yonex.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        return "client/home";
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "sortBy", defaultValue = "newest") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        int pageSize = 12;
        Page<Product> productPage = productService.searchProductsClient(keyword, categoryId, minPrice, maxPrice, sortBy, page, pageSize);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortBy", sortBy);

        return "client/product-list";
    }

    @GetMapping("/categories/{slug}")
    public String listByCategory(
            @PathVariable("slug") String slug,
            @RequestParam(name = "sortBy", defaultValue = "newest") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Category category = categoryService.getBySlug(slug);
        return listProducts(null, category.getCategoryId(), null, null, sortBy, page, model);
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getById(id);
        List<Review> reviews = reviewService.getReviewsForProduct(id);
        Double avgRating = reviewService.getAverageRatingForProduct(id);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        return "client/product-detail";
    }
}
