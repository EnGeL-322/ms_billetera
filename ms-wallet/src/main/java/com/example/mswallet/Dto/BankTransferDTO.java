package com.example.mswallet.Dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BankTransferDTO {
    private Long userId;
    private Long bankId;
    private BigDecimal amount;
}
