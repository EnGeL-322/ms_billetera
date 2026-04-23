package com.example.mswallet.Feign;

import com.example.mswallet.Dto.EventDTO;

import com.example.mswallet.Dto.EventUpdateSpentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "ms-events-service", path = "/events")
public interface EventFeignClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "eventByIdCB", fallbackMethod = "fallbackGetEventById")
    EventDTO getEventById(@PathVariable("id") Long id);

    @PutMapping("/{id}/spent")
    @CircuitBreaker(name = "updateEventSpentCB", fallbackMethod = "fallbackUpdateEventSpent")
    EventDTO updateEventSpent(
            @PathVariable("id") Long id,
            @RequestBody EventUpdateSpentDTO updateDTO
    );


    default EventDTO fallbackGetEventById(Long id, Throwable e) {
        System.err.println(" CircuitBreaker: ms-events no disponible (ID: " + id + ")");
        return EventDTO.builder()
                .id(0L)
                .name("Evento desconocido")
                .description("Servicio ms-events no disponible temporalmente")
                .budget(BigDecimal.ZERO)
                .spent(BigDecimal.ZERO)
                .startDate(null)
                .endDate(null)
                .build();
    }

    default EventDTO fallbackUpdateEventSpent(Long id, EventUpdateSpentDTO updateDTO, Throwable e) {
        System.err.println(" CircuitBreaker: no se pudo actualizar el gasto del evento (ID: " + id + ")");
        return EventDTO.builder()
                .id(id)
                .name("Evento no actualizado")
                .description("Fallo al conectar con ms-events")
                .budget(BigDecimal.ZERO)
                .spent(updateDTO != null && updateDTO.getAmount() != null ? updateDTO.getAmount() : BigDecimal.ZERO)
                .startDate(null)
                .endDate(null)
                .build();
    }

}
