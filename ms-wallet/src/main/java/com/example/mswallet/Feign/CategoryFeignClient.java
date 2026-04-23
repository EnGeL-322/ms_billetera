package com.example.mswallet.Feign;

import com.example.mswallet.Dto.CategoryDTO;
import com.example.mswallet.Dto.SubcategoryDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "ms-categories-service", path = "/categories")
public interface CategoryFeignClient {

    // ===============================
    // 🔹 OBTENER CATEGORÍA POR ID
    // ===============================
    @GetMapping("/{id}")
    @CircuitBreaker(name = "categoryByIdCB", fallbackMethod = "fallbackCategoryById")
    CategoryDTO getCategoryById(@PathVariable("id") Long id);

    // ===============================
    // 🔹 OBTENER SUBCATEGORÍA POR ID
    // ===============================
    @GetMapping("/{categoryId}/subcategories/{subId}")
    @CircuitBreaker(name = "subcategoryByIdCB", fallbackMethod = "fallbackSubcategoryById")
    SubcategoryDTO getSubcategoryById(
            @PathVariable("categoryId") Long categoryId,
            @PathVariable("subId") Long subcategoryId
    );

    // ===============================
    // 🔹 LISTAR CATEGORÍAS POR USUARIO
    // ===============================
    @GetMapping("/user/{userId}")
    @CircuitBreaker(name = "categoriesByUserCB", fallbackMethod = "fallbackCategoriesByUser")
    List<CategoryDTO> getCategoriesByUser(@PathVariable("userId") Long userId);


    // ===============================
    // 🔹 BUSCAR CATEGORÍA POR NOMBRE
    //   (NECESARIO PARA "objetivos")
    // ===============================
    @GetMapping("/search")
    @CircuitBreaker(name = "categoryByNameCB", fallbackMethod = "fallbackCategoryByName")
    CategoryDTO getCategoryByName(@RequestParam("name") String name);


    // ===============================
    // 🔹 ELIMINAR SUBCATEGORÍA
    //   (usado por metas)
    // ===============================
    @DeleteMapping("/subcategories/{subId}")
    @CircuitBreaker(name = "deleteSubcategoryCB", fallbackMethod = "fallbackDeleteSubcategory")
    void deleteSubcategory(@PathVariable("subId") Long subcategoryId);


    // ===========================================================
    // 🔻 FALLBACKS
    // ===========================================================

    default CategoryDTO fallbackCategoryById(Long id, Throwable e) {
        System.err.println("⚠ CircuitBreaker: categoría no disponible (ID: " + id + ")");
        CategoryDTO dto = new CategoryDTO();
        dto.setId(0L);
        dto.setName("Categoría no disponible");
        return dto;
    }

    default SubcategoryDTO fallbackSubcategoryById(Long categoryId, Long subcategoryId, Throwable e) {
        System.err.println("⚠ CircuitBreaker: subcategoría no disponible (ID: " + subcategoryId + ")");
        SubcategoryDTO dto = new SubcategoryDTO();
        dto.setId(0L);
        dto.setName("Subcategoría no disponible");
        return dto;
    }

    default List<CategoryDTO> fallbackCategoriesByUser(Long userId, Throwable e) {
        System.err.println("⚠ CircuitBreaker: no se pudieron obtener categorías del usuario " + userId);
        return Collections.emptyList();
    }

    default CategoryDTO fallbackCategoryByName(String name, Throwable e) {
        System.err.println("⚠ CircuitBreaker: no se pudo obtener categoría por nombre: " + name);
        CategoryDTO dto = new CategoryDTO();
        dto.setId(0L);
        dto.setName("No disponible");
        return dto;
    }

    default void fallbackDeleteSubcategory(Long subId, Throwable e) {
        System.err.println("⚠ CircuitBreaker: no se pudo eliminar la subcategoría " + subId);
    }
}
