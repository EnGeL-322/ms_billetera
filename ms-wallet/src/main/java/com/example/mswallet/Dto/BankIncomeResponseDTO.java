package com.example.mswallet.Dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankIncomeResponseDTO {
    private Long id;
    private Long userId;
    private Long bankId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}
