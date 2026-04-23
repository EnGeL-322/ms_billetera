package com.example.mswallet.Feign;

import com.example.mswallet.Dto.GoalResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "ms-goals-service", path = "/goals")
public interface GoalFeignClient {

    // ==============================================
    // 🔹 ACTUALIZAR EL MONTO DE UNA META (aporte)
    // ==============================================
    @PutMapping("/{goalId}/amount")
    @CircuitBreaker(name = "goalUpdateAmountCB", fallbackMethod = "fallbackUpdateGoalAmount")
    GoalResponseDTO updateGoalAmount(
            @PathVariable("goalId") Long goalId,
            @RequestParam("amountChange") BigDecimal amountChange
    );


    // ==============================================
    // 🔻 FALLBACK
    // ==============================================
    default GoalResponseDTO fallbackUpdateGoalAmount(Long goalId, BigDecimal amountChange, Throwable e) {
        System.err.println("⚠ CircuitBreaker: ms-goals no disponible para actualizar meta " + goalId);

        GoalResponseDTO dto = new GoalResponseDTO();
        dto.setId(goalId);
        dto.setName("Meta no disponible");
        dto.setDescription("No se pudo actualizar temporalmente");
        return dto;
    }
}
