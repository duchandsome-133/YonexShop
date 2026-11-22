package com.yonex.shop.controller.admin;

import com.yonex.shop.dto.RegisterRequest;
import com.yonex.shop.entity.User;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/users")
    public String listCustomers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<User> userPage = userService.getCustomers(keyword, PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    @GetMapping("/staff")
    public String listStaff(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<User> staffPage = userService.getStaffs(PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("staffs", staffPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());

        if (!model.containsAttribute("staffRequest")) {
            model.addAttribute("staffRequest", new RegisterRequest());
        }
        return "admin/staff";
    }

    @PostMapping("/staff/create")
    public String createStaff(
            @Valid @ModelAttribute("staffRequest") RegisterRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return listStaff(0, model);
        }

        try {
            userService.createStaff(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo tài khoản nhân viên thành công!");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái tài khoản.");
        return "redirect:/admin/users";
    }
}
