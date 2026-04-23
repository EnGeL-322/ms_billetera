package com.example.msgoals.DTO;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private Long userId;
    private String name;
    private String icon;
    private String color;
}
