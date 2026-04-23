package com.example.msevents.Repository;

import com.example.msevents.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // 🔍 Permite obtener todos los eventos de un usuario específico
    List<Event> findByUserId(Long userId);
}
