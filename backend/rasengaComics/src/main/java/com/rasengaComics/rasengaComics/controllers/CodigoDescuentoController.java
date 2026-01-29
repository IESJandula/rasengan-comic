package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.CodigoDescuentoService;
import com.rasengaComics.rasengaComics.models.CodigoDescuento;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/codigos-descuento")
public class CodigoDescuentoController {

    private final CodigoDescuentoService codigoService;

    public CodigoDescuentoController(CodigoDescuentoService codigoService) {
        this.codigoService = codigoService;
    }

    @GetMapping
    public ResponseEntity<List<CodigoDescuento>> listar() {
        return ResponseEntity.ok(codigoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<CodigoDescuento> codigo = codigoService.obtener(id);
        if (codigo.isPresent()) {
            return ResponseEntity.ok(codigo.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CodigoDescuento codigo) {
        try {
            CodigoDescuento creado = codigoService.guardar(codigo);
            return ResponseEntity.status(201).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Optional<CodigoDescuento> codigo = codigoService.obtener(id);
            if (codigo.isPresent()) {
                codigoService.eliminar(id);
                return ResponseEntity.ok(new ApiResponse(true, "C\u00f3digo de descuento eliminado", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo) {
        boolean valido = codigoService.validarCodigo(codigo);
        return ResponseEntity.ok(new ApiResponse(valido, valido ? "C\u00f3digo v\u00e1lido" : "C\u00f3digo inv\u00e1lido", null));
    }
}

