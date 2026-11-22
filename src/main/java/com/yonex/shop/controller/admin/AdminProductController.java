package com.yonex.shop.controller.admin;

import com.yonex.shop.dto.ProductRequest;
import com.yonex.shop.entity.Category;
import com.yonex.shop.entity.Product;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.service.CategoryService;
import com.yonex.shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<Product> productPage = productService.searchProductsAdmin(
                keyword, categoryId, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.getAll());

        return "admin/products";
    }

    @GetMapping("/create")
    public String createProductForm(Model model) {
        if (!model.containsAttribute("productRequest")) {
            model.addAttribute("productRequest", new ProductRequest());
        }
        model.addAttribute("categories", categoryService.getAllActive());
        return "admin/product-form";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("productRequest") ProductRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllActive());
            return "admin/product-form";
        }

        try {
            productService.createProduct(request, imageFiles);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới sản phẩm thành công!");
            return "redirect:/admin/products";
        } catch (BadRequestException ex) {
            bindingResult.rejectValue("sku", "error.productRequest", ex.getMessage());
            model.addAttribute("categories", categoryService.getAllActive());
            return "admin/product-form";
        } catch (Exception ex) {
            bindingResult.reject("globalError", ex.getMessage());
            model.addAttribute("categories", categoryService.getAllActive());
            return "admin/product-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getById(id);

        ProductRequest request = ProductRequest.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategory().getCategoryId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .active(product.isActive())
                .build();

        model.addAttribute("product", product);
        model.addAttribute("productRequest", request);
        model.addAttribute("categories", categoryService.getAllActive());
        return "admin/product-form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("productRequest") ProductRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Product product = productService.getById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllActive());
            return "admin/product-form";
        }

        try {
            productService.updateProduct(id, request, imageFiles);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/products";
        } catch (BadRequestException ex) {
            bindingResult.rejectValue("sku", "error.productRequest", ex.getMessage());
            Product product = productService.getById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllActive());
            return "admin/product-form";
        }
    }

    @PostMapping("/toggle/{id}")
    public String toggleProductStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.toggleProductStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái kinh doanh của sản phẩm.");
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã ngừng kinh doanh sản phẩm.");
        return "redirect:/admin/products";
    }
}
