package com.example.mswallet.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_bank_balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long bankId;

    private BigDecimal balance;   // saldo actual del banco
}
