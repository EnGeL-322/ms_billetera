package com.example.msgoals.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequestDTO {

    private Long userId;
    private String name;
    private String description;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;

    private LocalDate deadline;  // ✔ fecha correcta (solo día-mes-año)

    private String status;
}
