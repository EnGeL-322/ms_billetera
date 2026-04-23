package com.example.mscategories.Service;

import com.example.mscategories.DTO.*;
import com.example.mscategories.Entity.Category;
import com.example.mscategories.Entity.Subcategory;
import com.example.mscategories.Exceptions.ResourceNotFoundException;
import com.example.mscategories.Feign.UserFeignClient;
import com.example.mscategories.Repository.CategoryRepository;
import com.example.mscategories.Repository.SubcategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserFeignClient userFeignClient;

    private static final String PROTECTED_CATEGORY = "objetivos";

    // ------------------------------------------------------------
    // 🔹 Crear categoría
    // ------------------------------------------------------------
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {

        try {
            AuthUserDto user = userFeignClient.getUserById(request.getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("Usuario no encontrado");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Usuario no encontrado o ms-auth no disponible");
        }

        Category category = Category.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .icon(request.getIcon())
                .color(request.getColor())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    // 🔹 Obtener categorías por usuario
    public List<CategoryResponseDTO> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Obtener una categoría por ID
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return mapToResponse(category);
    }

    // 🔹 Actualizar categoría
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // 🚫 No editar categoría protegida
        if (category.getName().equalsIgnoreCase(PROTECTED_CATEGORY)) {
            throw new IllegalStateException("La categoría 'objetivos' no se puede editar.");
        }

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());

        return mapToResponse(categoryRepository.save(category));
    }

    // 🔹 Eliminar categoría
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // 🚫 No eliminar objetivos
        if (category.getName().equalsIgnoreCase(PROTECTED_CATEGORY)) {
            throw new IllegalStateException("La categoría 'objetivos' no se puede eliminar.");
        }

        // Eliminar subcategorías
        subcategoryRepository.deleteAll(
                subcategoryRepository.findByCategoryId(id)
        );

        categoryRepository.delete(category);
    }

    // 🔹 Crear subcategoría
    @Transactional
    public SubcategoryResponseDTO createSubcategory(Long categoryId, SubcategoryRequestDTO request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Subcategory sub = Subcategory.builder()
                .category(category)
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .build();

        return mapSubcategory(subcategoryRepository.save(sub));
    }

    // 🔹 Listar subcategorías
    public List<SubcategoryResponseDTO> getSubcategories(Long categoryId) {
        return subcategoryRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapSubcategory)
                .collect(Collectors.toList());
    }

    // 🔹 Obtener subcategoría por ID
    public SubcategoryResponseDTO getSubcategoryById(Long categoryId, Long subId) {
        Subcategory sub = subcategoryRepository.findById(subId)
                .filter(s -> s.getCategory().getId().equals(categoryId))
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada"));

        return mapSubcategory(sub);
    }

    // 🔹 Actualizar subcategoría
    @Transactional
    public SubcategoryResponseDTO updateSubcategory(Long categoryId, Long subId, SubcategoryRequestDTO request) {

        Subcategory sub = subcategoryRepository.findById(subId)
                .filter(s -> s.getCategory().getId().equals(categoryId))
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada"));

        sub.setName(request.getName());

        return mapSubcategory(subcategoryRepository.save(sub));
    }

    // 🔹 Eliminar subcategoría
    @Transactional
    public void deleteSubcategory(Long categoryId, Long subId) {

        Subcategory sub = subcategoryRepository.findById(subId)
                .filter(s -> s.getCategory().getId().equals(categoryId))
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada"));

        subcategoryRepository.delete(sub);
    }

    // ------------------------------------------------------------
    // 🔹 Buscar categoría por NOMBRE
    //    (para GoalsService ✔)
    // ------------------------------------------------------------
    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + name));
    }

    // 🔹 Mapper categoría
    private CategoryResponseDTO mapToResponse(Category c) {
        return CategoryResponseDTO.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .name(c.getName())
                .icon(c.getIcon())
                .color(c.getColor())
                .createdAt(c.getCreatedAt())
                .build();
    }

    // 🔹 Mapper subcategoría
    private SubcategoryResponseDTO mapSubcategory(Subcategory s) {
        return SubcategoryResponseDTO.builder()
                .id(s.getId())
                .categoryId(s.getCategory().getId())
                .name(s.getName())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
