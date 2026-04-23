package com.example.msgoals.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalUpdateDTO {
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private LocalDate deadline;   // ✔ corregido
    private String status;
}
