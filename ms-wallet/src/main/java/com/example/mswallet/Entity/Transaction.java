package com.example.mswallet.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long walletId;
    private Long userId;
    private Long categoryId; // Referencia a ms-categories
    private Long subcategoryId; // Referencia a ms-categories
    private Long eventId; // Referencia a ms-events (opcional)
    private Long goalId;
    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME, EXPENSE
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
    public enum TransactionType {
        INCOME, EXPENSE
    }
}

