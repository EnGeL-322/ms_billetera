package com.example.msevents.Controller;

import com.example.msevents.DTO.CreateEventDTO;
import com.example.msevents.DTO.EventResponseDTO;
import com.example.msevents.DTO.EventUpdateSpentDTO;
import com.example.msevents.Service.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventService;

    // -------------------------------------------------------------------------
    // 🟦 Crear un evento
    // -------------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<EventResponseDTO> create(@RequestBody CreateEventDTO dto) {
        return ResponseEntity.ok(eventService.createEvent(dto));
    }

    // -------------------------------------------------------------------------
    // 🟧 Listar todos los eventos
    // -------------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAll() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // -------------------------------------------------------------------------
    // 🟩 Obtener evento por ID
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // -------------------------------------------------------------------------
    // 🟨 Obtener eventos de un usuario
    // -------------------------------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getEventsByUserId(userId));
    }

    // -------------------------------------------------------------------------
    // 🟪 Actualizar evento completo
    // -------------------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> update(
            @PathVariable Long id,
            @RequestBody CreateEventDTO dto) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto));
    }

    // -------------------------------------------------------------------------
    // 🟥 Sumar gasto (usado por el front Angular)
    // -------------------------------------------------------------------------
    @PatchMapping("/{id}/spent")
    public ResponseEntity<EventResponseDTO> updateSpent(
            @PathVariable Long id,
            @RequestBody EventUpdateSpentDTO dto) {
        return ResponseEntity.ok(eventService.updateEventSpent(id, dto));
    }

    // -------------------------------------------------------------------------
    // 🟩 Tu método adicional (llamado desde ms-wallet)
    // PUT /events/{id}/spent
    // -------------------------------------------------------------------------
    @PutMapping("/{id}/spent")
    public ResponseEntity<EventResponseDTO> updateEventSpentFromWallet(
            @PathVariable Long id,
            @RequestBody EventUpdateSpentDTO updateDTO) {
        EventResponseDTO response = eventService.updateEventSpent(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------------
    // ⛔ Eliminar evento
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Evento eliminado correctamente");
    }
}
