package com.example.mswallet.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_bank_income")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long bankId;

    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}