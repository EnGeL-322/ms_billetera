package com.example.msevents.Service;

import com.example.msevents.DTO.CreateEventDTO;
import com.example.msevents.DTO.EventResponseDTO;
import com.example.msevents.DTO.EventUpdateSpentDTO;
import com.example.msevents.Entity.Event;
import com.example.msevents.Feign.UserFeignClient;
import com.example.msevents.Repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl {

    // 🧩 Acceso a la BD
    private final EventRepository eventRepository;

    // 🔗 Cliente Feign para consultar usuario en ms-auth/ms-users
    private final UserFeignClient userClient;

    // -------------------------------------------------------------------------
    // 🟦 Crear un evento
    // -------------------------------------------------------------------------
    public EventResponseDTO createEvent(CreateEventDTO request) {

        // 🔐 Validar que el usuario exista en ms-users
        validateUser(request.getUserId());

        // 🏗 Construimos un evento nuevo
        Event event = Event.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .description(request.getDescription())
                .budget(request.getBudget())
                .spent(BigDecimal.ZERO)   // Siempre inicia en 0
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        // 💾 Guardamos en BD
        event = eventRepository.save(event);

        // 🔁 Convertimos entidad → DTO para devolver al frontend
        return mapToResponse(event);
    }

    // -------------------------------------------------------------------------
    // 🟩 Buscar un evento por su ID
    // -------------------------------------------------------------------------
    public EventResponseDTO getEventById(Long id) {

        // Buscar evento; si no existe, lanzar error
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        return mapToResponse(event);
    }

    // -------------------------------------------------------------------------
    // 🟧 Listar todos los eventos
    // -------------------------------------------------------------------------
    public List<EventResponseDTO> getAllEvents() {

        return eventRepository.findAll()      // Obtener todos
                .stream()
                .map(this::mapToResponse)     // Convertir a DTO
                .toList();
    }

    // -------------------------------------------------------------------------
    // 🟨 Listar eventos por usuario
    // -------------------------------------------------------------------------
    public List<EventResponseDTO> getEventsByUserId(Long userId) {

        return eventRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -------------------------------------------------------------------------
    // 🟪 Actualizar un evento completo
    // -------------------------------------------------------------------------
    public EventResponseDTO updateEvent(Long id, CreateEventDTO dto) {

        // Validar que el evento exista
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Validar que el usuario exista
        validateUser(dto.getUserId());

        // Actualizamos información
        event.setUserId(dto.getUserId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setBudget(dto.getBudget());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());

        // Guardamos información modificada
        eventRepository.save(event);

        return mapToResponse(event);
    }

    // -------------------------------------------------------------------------
    // 🟥 Sumar gasto a un evento
    // -------------------------------------------------------------------------
    public EventResponseDTO updateEventSpent(Long id, EventUpdateSpentDTO dto) {

        // Obtener el evento
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Sumar monto al gasto actual
        event.setSpent(event.getSpent().add(dto.getAmount()));

        // Guardar cambios
        eventRepository.save(event);

        return mapToResponse(event);
    }

    // -------------------------------------------------------------------------
    // ⛔ Eliminar evento por ID
    // -------------------------------------------------------------------------
    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        eventRepository.delete(event); // Eliminar de la BD
    }

    // -------------------------------------------------------------------------
    // 🔐 Validar que un usuario exista llamando a ms-users
    // -------------------------------------------------------------------------
    private void validateUser(Long userId) {
        try {
            userClient.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Usuario no encontrado en ms-users");
        }
    }

    // -------------------------------------------------------------------------
    // 🔄 Convertir Entity → DTO (para respuestas al frontend)
    // -------------------------------------------------------------------------
    private EventResponseDTO mapToResponse(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .name(event.getName())
                .description(event.getDescription())
                .budget(event.getBudget())
                .spent(event.getSpent())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
