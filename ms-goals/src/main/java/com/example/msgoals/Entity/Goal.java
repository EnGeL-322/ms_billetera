package com.example.msgoals.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal currentAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal progress;

    private LocalDateTime deadline;
    private String status;
    private LocalDateTime createdAt;
    private Long subcategoryId;

}
