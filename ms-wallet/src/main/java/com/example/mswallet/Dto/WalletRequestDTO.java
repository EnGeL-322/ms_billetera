package com.example.mswallet.Dto;

import lombok.Data;

@Data
public class WalletRequestDTO {
    private Long userId;
    private String currency;
}