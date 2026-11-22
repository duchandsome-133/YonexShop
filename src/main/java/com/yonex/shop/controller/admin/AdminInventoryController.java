package com.yonex.shop.controller.admin;

import com.yonex.shop.config.CustomUserDetails;
import com.yonex.shop.entity.Inventory;
import com.yonex.shop.entity.InventoryTransaction;
import com.yonex.shop.entity.ProductVariant;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.repository.InventoryRepository;
import com.yonex.shop.repository.InventoryTransactionRepository;
import com.yonex.shop.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    @GetMapping
    public String inventoryList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<ProductVariant> variantPage = productVariantRepository.findAll(
                PageRequest.of(page, 15, Sort.by(Sort.Direction.ASC, "product.name"))
        );

        model.addAttribute("variants", variantPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", variantPage.getTotalPages());
        return "admin/inventory";
    }

    @PostMapping("/adjust")
    @Transactional
    public String adjustStock(
            @RequestParam("variantId") Long variantId,
            @RequestParam("changeQuantity") int changeQuantity,
            @RequestParam("reason") String reason,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BadRequestException("Biến thể không tồn tại"));

        Inventory inventory = inventoryRepository.findByVariant(variant)
                .orElseGet(() -> Inventory.builder()
                        .variant(variant)
                        .quantity(0)
                        .updatedAt(LocalDateTime.now())
                        .build());

        int newQuantity = inventory.getQuantity() + changeQuantity;
        if (newQuantity < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể điều chỉnh tồn kho âm!");
            return "redirect:/admin/inventory";
        }

        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);

        InventoryTransaction tx = InventoryTransaction.builder()
                .variant(variant)
                .changeQuantity(changeQuantity)
                .reason(reason.trim())
                .createdBy(userDetails.getUsername())
                .build();
        inventoryTransactionRepository.save(tx);

        redirectAttributes.addFlashAttribute("successMessage", 
                "Điều chỉnh kho cho '" + variant.getVariantName() + "' thành công (Tồn mới: " + newQuantity + ")");
        return "redirect:/admin/inventory";
    }
}
