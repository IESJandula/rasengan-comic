package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.NotificacionService;
import com.rasengaComics.rasengaComics.models.Notificacion;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Notificacion> notificacion = notificacionService.obtener(id);
        if (notificacion.isPresent()) {
            return ResponseEntity.ok(notificacion.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Notificacion notificacion) {
        try {
            Notificacion creada = notificacionService.guardar(notificacion);
            return ResponseEntity.status(201).body(creada);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok(new ApiResponse(true, "Notificaci\u00f3n marcada como le\u00edda", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Optional<Notificacion> notificacion = notificacionService.obtener(id);
            if (notificacion.isPresent()) {
                notificacionService.eliminar(id);
                return ResponseEntity.ok(new ApiResponse(true, "Notificaci\u00f3n eliminada", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

