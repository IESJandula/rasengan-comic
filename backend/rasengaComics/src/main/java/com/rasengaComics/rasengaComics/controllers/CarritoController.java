package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.CarritoService;
import com.rasengaComics.rasengaComics.models.Carrito;
import com.rasengaComics.rasengaComics.dto.request.CarritoRequest;
import com.rasengaComics.rasengaComics.dto.response.CarritoResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // Obtener carrito del usuario autenticado
    @GetMapping("/{usuarioUid}")
    public ResponseEntity<?> obtener(@PathVariable String usuarioUid,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Carrito carrito = carritoService.obtenerOCrearCarrito(usuarioUid);
            return ResponseEntity.ok(toResponse(carrito));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Guardar carrito (después de agregar/remover items)
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody CarritoRequest req,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Carrito carrito = carritoService.obtenerOCrearCarrito(req.getUsuarioUid());
            // La lógica de agregar items se haría aquí
            // Por ahora, solo guardamos el carrito
            Carrito guardado = carritoService.guardar(carrito);
            return ResponseEntity.ok(toResponse(guardado));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Vaciar carrito
    @DeleteMapping("/{carritoId}")
    public ResponseEntity<?> vaciar(@PathVariable Long carritoId,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            carritoService.vaciarCarrito(carritoId);
            return ResponseEntity.ok(new ApiResponse(true, "Carrito vaciado", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    private CarritoResponse toResponse(Carrito c) {
        if (c == null) return null;
        CarritoResponse r = new CarritoResponse();
        r.setId(c.getId());
        r.setUsuarioUid(c.getUsuario().getUid());
        r.setTotal(carritoService.calcularTotal(c));
        r.setCantidadItems(c.getItems() != null ? c.getItems().size() : 0);
        return r;
    }
}

