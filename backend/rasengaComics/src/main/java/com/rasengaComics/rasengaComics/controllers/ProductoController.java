package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.ProductoService;
import com.rasengaComics.rasengaComics.dto.request.ProductoRequest;
import com.rasengaComics.rasengaComics.dto.response.ProductoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return service.listarTodos().stream().map(ProductoService::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return service.obtener(id)
                .map(ProductoService::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest producto) {
        try {
            ProductoResponse creado = service.guardarDesdeRequest(producto);
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            com.rasengaComics.rasengaComics.dto.response.ErrorResponse err = new com.rasengaComics.rasengaComics.dto.response.ErrorResponse();
            err.setMessage("Error al crear producto: " + e.getMessage());
            err.setDetails(e.toString());
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // FK constraint or other integrity issue
            return ResponseEntity.status(409).build();
        }
    }
}

