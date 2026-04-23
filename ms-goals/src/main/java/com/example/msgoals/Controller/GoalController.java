package com.example.msgoals.Controller;

import com.example.msgoals.DTO.GoalRequestDTO;
import com.example.msgoals.DTO.GoalResponseDTO;
import com.example.msgoals.DTO.GoalUpdateDTO;
import com.example.msgoals.Service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponseDTO> createGoal(@RequestBody GoalRequestDTO request) {
        GoalResponseDTO response = goalService.createGoal(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalResponseDTO>> getGoalsByUser(@PathVariable Long userId) {
        List<GoalResponseDTO> goals = goalService.getGoalsByUser(userId);
        return ResponseEntity.ok(goals);
    }
    @PutMapping("/{goalId}/amount")
    public ResponseEntity<GoalResponseDTO> updateGoalAmount(
            @PathVariable Long goalId,
            @RequestParam BigDecimal amountChange) {
        GoalResponseDTO updatedGoal = goalService.updateGoalAmount(goalId, amountChange);
        return ResponseEntity.ok(updatedGoal);
    }
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestBody GoalUpdateDTO request
    ) {
        return ResponseEntity.ok(goalService.updateGoal(goalId, request));
    }
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }


}
