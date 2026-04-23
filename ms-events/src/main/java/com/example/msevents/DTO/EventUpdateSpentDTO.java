package com.example.msevents.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EventUpdateSpentDTO {
    private Long eventId;
    private BigDecimal amount;
}
