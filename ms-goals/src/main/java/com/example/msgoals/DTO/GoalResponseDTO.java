package com.example.msgoals.DTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDTO {
    private Long id;
    private Long userId;

    private String name;
    private String description;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal progress;

    private LocalDate deadline;     // ✔ corregido

    private String status;
    private LocalDateTime createdAt;

    private Long subcategoryId;
}
