package com.yonex.shop.controller.admin;

import com.yonex.shop.dto.CategoryRequest;
import com.yonex.shop.entity.Category;
import com.yonex.shop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        if (!model.containsAttribute("categoryRequest")) {
            model.addAttribute("categoryRequest", new CategoryRequest());
        }
        return "admin/categories";
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAll());
            return "admin/categories";
        }

        try {
            categoryService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("categoryRequest") CategoryRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            categoryService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        categoryService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái danh mục.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa / ẩn danh mục.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
