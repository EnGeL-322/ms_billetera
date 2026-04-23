package com.example.msgoals.Service;

import com.example.msgoals.DTO.*;
import com.example.msgoals.Entity.Goal;
import com.example.msgoals.Exceptions.ResourceNotFoundException;
import com.example.msgoals.Feign.CategoryFeignClient;
import com.example.msgoals.Feign.UserFeignClient;
import com.example.msgoals.Repository.GoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserFeignClient userFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    // =========================================================
    // CREAR META
    // =========================================================
    @Transactional
    public GoalResponseDTO createGoal(GoalRequestDTO request) {

        // Convertir fecha a LocalDateTime (inicio del día)
        LocalDateTime deadline = request.getDeadline() != null
                ? request.getDeadline().atStartOfDay()
                : null;

        // 🔥 1. Obtener la categoría "objetivos" del usuario
        CategoryDTO objetivos = categoryFeignClient.getCategoryByNameAndUser(
                "objetivos",
                request.getUserId()
        );

        // 🔥 2. Crear subcategoría dentro de esa categoría
        SubcategoryResponseDTO sub = categoryFeignClient.createSubcategory(
                objetivos.getId(),
                new SubcategoryRequestDTO(
                        request.getName(),
                        "emoji_objects"
                )
        );

        // 🔥 3. Crear entidad Meta
        Goal goal = Goal.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .currentAmount(request.getCurrentAmount())
                .subcategoryId(sub.getId())
                .progress(calculateProgress(request.getCurrentAmount(), request.getTargetAmount()))
                .deadline(deadline)
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(goalRepository.save(goal));
    }

    // =========================================================
    // OBTENER METAS POR USUARIO
    // =========================================================
    public List<GoalResponseDTO> getGoalsByUser(Long userId) {
        return goalRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // APORTAR MONTO
    // =========================================================
    @Transactional
    public GoalResponseDTO updateGoalAmount(Long goalId, BigDecimal amountChange) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada"));

        goal.setCurrentAmount(goal.getCurrentAmount().add(amountChange));

        if (goal.getCurrentAmount().compareTo(BigDecimal.ZERO) < 0) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        }

        goal.setProgress(calculateProgress(goal.getCurrentAmount(), goal.getTargetAmount()));

        return mapToResponse(goalRepository.save(goal));
    }

    // =========================================================
    // CALCULAR % AVANCE
    // =========================================================
    private BigDecimal calculateProgress(BigDecimal current, BigDecimal target) {

        if (target.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return current.multiply(BigDecimal.valueOf(100))
                .divide(target, 2, RoundingMode.HALF_UP);
    }

    // =========================================================
    // MAPEAR ENTITY → DTO
    // =========================================================
    private GoalResponseDTO mapToResponse(Goal goal) {

        LocalDate deadline = goal.getDeadline() != null
                ? goal.getDeadline().toLocalDate()
                : null;

        return GoalResponseDTO.builder()
                .id(goal.getId())
                .userId(goal.getUserId())
                .name(goal.getName())
                .description(goal.getDescription())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .progress(goal.getProgress())
                .subcategoryId(goal.getSubcategoryId())
                .deadline(deadline)
                .status(goal.getStatus())
                .createdAt(goal.getCreatedAt())
                .build();
    }

    // =========================================================
    // ELIMINAR META + SUBCATEGORÍA DINÁMICA
    // =========================================================
    @Transactional
    public void deleteGoal(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada"));

        try {
            // 🔥 Obtener categoría "objetivos" DINÁMICAMENTE
            CategoryDTO objetivos = categoryFeignClient.getCategoryByNameAndUser(
                    "objetivos",
                    goal.getUserId()
            );

            // 🔥 Eliminar subcategoría exacta
            categoryFeignClient.deleteSubcategory(objetivos.getId(), goal.getSubcategoryId());

        } catch (Exception e) {
            log.warn("⚠ No se pudo borrar la subcategoría vinculada. La meta igual se eliminará.");
        }

        goalRepository.delete(goal);
    }
    @Transactional
    public GoalResponseDTO updateGoal(Long goalId, GoalUpdateDTO request) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada"));

        if (request.getName() != null) goal.setName(request.getName());
        if (request.getDescription() != null) goal.setDescription(request.getDescription());

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
            goal.setProgress(calculateProgress(
                    goal.getCurrentAmount(),
                    request.getTargetAmount()
            ));
        }

        if (request.getDeadline() != null)
            goal.setDeadline(request.getDeadline().atStartOfDay());

        if (request.getStatus() != null)
            goal.setStatus(request.getStatus());

        Goal updated = goalRepository.save(goal);

        return mapToResponse(updated);
    }

}

