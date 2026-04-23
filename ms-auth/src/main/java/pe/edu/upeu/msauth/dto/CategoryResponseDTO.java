package pe.edu.upeu.msauth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String icon;
    private String color;
}
