package com.example.msevents.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private BigDecimal budget;
    private BigDecimal spent;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
}
