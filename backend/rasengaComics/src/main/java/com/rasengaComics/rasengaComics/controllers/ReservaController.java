package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.ReservaService;
import com.rasengaComics.rasengaComics.models.Reserva;
import com.rasengaComics.rasengaComics.dto.request.ReservaRequest;
import com.rasengaComics.rasengaComics.dto.response.ReservaResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listar(@RequestParam(value = "email", required = false) String email) {
        if (email != null && !email.isEmpty()) {
            return ResponseEntity.ok(reservaService.listarPorUsuarioEmail(email));
        }
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.obtener(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok(reserva.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req) {
        try {
            return ResponseEntity.status(501).body(new ApiResponse(false, "Creación de reservas requiere más lógica", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Optional<Reserva> reserva = reservaService.obtener(id);
            if (reserva.isPresent()) {
                reservaService.eliminar(id);
                return ResponseEntity.ok(new ApiResponse(true, "Reserva eliminada", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

