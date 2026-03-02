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
@RequestMapping("/api/discounts")
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

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CodigoDescuento codigo) {
        try {
            Optional<CodigoDescuento> existente = codigoService.obtener(id);
            if (existente.isPresent()) {
                codigo.setId(id);
                CodigoDescuento actualizado = codigoService.guardar(codigo);
                return ResponseEntity.ok(actualizado);
            }
            return ResponseEntity.notFound().build();
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
                return ResponseEntity.ok(new ApiResponse(true, "Código de descuento eliminado", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo) {
        boolean valido = codigoService.validarCodigo(codigo);
        return ResponseEntity.ok(new ApiResponse(valido, valido ? "Código válido" : "Código inválido", null));
    }

    @PostMapping("/aplicar")
    public ResponseEntity<?> aplicarDescuento(@RequestParam String codigo, @RequestParam Double precio) {
        try {
            Optional<CodigoDescuento> optCodigo = codigoService.obtenerPorCodigo(codigo);
            if (optCodigo.isEmpty()) {
                return ResponseEntity.status(404).body(new ApiResponse(false, "Código no encontrado", null));
            }

            CodigoDescuento cd = optCodigo.get();
            if (!codigoService.validarCodigo(codigo)) {
                return ResponseEntity.status(400).body(new ApiResponse(false, "Código no válido o expirado", null));
            }

            Double descuento = codigoService.calcularDescuento(cd, precio);
            return ResponseEntity.ok(new ApiResponse(true, "Descuento aplicado", descuento));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

