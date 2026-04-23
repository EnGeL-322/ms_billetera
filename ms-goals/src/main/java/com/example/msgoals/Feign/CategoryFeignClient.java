package com.example.msgoals.Feign;

import com.example.msgoals.DTO.CategoryDTO;
import com.example.msgoals.DTO.SubcategoryRequestDTO;
import com.example.msgoals.DTO.SubcategoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-categories-service", path = "/categories")
public interface CategoryFeignClient {

    // =========================================================
    // OBTENER CATEGORÍA "OBJETIVOS" POR NOMBRE + USUARIO
    // =========================================================
    @GetMapping("/search")
    CategoryDTO getCategoryByNameAndUser(
            @RequestParam String name,
            @RequestParam Long userId
    );

    // =========================================================
    // CREAR SUBCATEGORÍA DENTRO DE UNA CATEGORÍA
    // =========================================================
    @PostMapping("/{categoryId}/subcategories")
    SubcategoryResponseDTO createSubcategory(
            @PathVariable Long categoryId,
            @RequestBody SubcategoryRequestDTO request
    );

    // =========================================================
    // ELIMINAR SUBCATEGORÍA (Correcto: categoryId + subcategoryId)
    // =========================================================
    @DeleteMapping("/{categoryId}/subcategories/{subId}")
    void deleteSubcategory(
            @PathVariable Long categoryId,
            @PathVariable("subId") Long subcategoryId
    );
}
