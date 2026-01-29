package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.FavoritoService;
import com.rasengaComics.rasengaComics.models.Favorito;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public ResponseEntity<List<Favorito>> listar() {
        return ResponseEntity.ok(favoritoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Favorito> favorito = favoritoService.obtener(id);
        if (favorito.isPresent()) {
            return ResponseEntity.ok(favorito.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            String usuarioUid = (String) body.get("usuarioUid");
            Long productoId = Long.valueOf(body.get("productoId").toString());
            favoritoService.agregarFavorito(usuarioUid, productoId);
            return ResponseEntity.status(201).body(new ApiResponse(true, "Favorito agregado", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Optional<Favorito> favorito = favoritoService.obtener(id);
            if (favorito.isPresent()) {
                favoritoService.eliminar(id);
                return ResponseEntity.ok(new ApiResponse(true, "Favorito eliminado", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

