package com.example.mswallet.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubcategoryDTO {
    private Long id;
    private Long categoryId;
    private String name;
}