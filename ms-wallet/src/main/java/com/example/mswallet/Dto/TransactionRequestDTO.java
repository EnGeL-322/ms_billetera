package com.example.mswallet.Dto;

import com.example.mswallet.Entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {
    private Long userId;
    private Long categoryId;
    private Long subcategoryId;
    private Long eventId; // Opcional
    private Long goalId;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private String description;
}