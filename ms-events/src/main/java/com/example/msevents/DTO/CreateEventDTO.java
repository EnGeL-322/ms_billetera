package com.example.msevents.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventDTO {
    private Long userId;
    private String name;
    private String description;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;
}
