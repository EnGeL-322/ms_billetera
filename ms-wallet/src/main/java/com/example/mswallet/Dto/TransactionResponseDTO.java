package com.example.mswallet.Dto;

import com.example.mswallet.Entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {
    private Long id;
    private Long walletId;
    private Long userId;
    private CategoryDTO category;
    private SubcategoryDTO subcategory;
    private EventDTO event;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;

    // ⭐ AGREGADO (NO EXISTÍA)
    private BigDecimal newBankBalance;
    private BigDecimal newWalletBalance;
}
