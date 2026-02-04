package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.ResenaService;
import com.rasengaComics.rasengaComics.models.Resena;
import com.rasengaComics.rasengaComics.dto.request.ResenaRequest;
import com.rasengaComics.rasengaComics.dto.response.ResenaResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public ResponseEntity<List<Resena>> listar() {
        return ResponseEntity.ok(resenaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Resena> resena = resenaService.obtener(id);
        if (resena.isPresent()) {
            return ResponseEntity.ok(resena.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ResenaRequest req) {
        try {
            return ResponseEntity.status(501).body(new ApiResponse(false, "Creación de reseñas requiere más lógica", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Optional<Resena> resena = resenaService.obtener(id);
            if (resena.isPresent()) {
                resenaService.eliminar(id);
                return ResponseEntity.ok(new ApiResponse(true, "Reseña eliminada", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

