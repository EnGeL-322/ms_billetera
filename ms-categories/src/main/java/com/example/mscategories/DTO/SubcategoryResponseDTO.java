package com.example.mscategories.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoryResponseDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private LocalDateTime createdAt;
}
