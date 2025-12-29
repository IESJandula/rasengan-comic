package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.response.PedidoResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest req) {
        // entidad Pedido no implementada completamente -> devolver 501 informativo
        return ResponseEntity.status(501).body(new ApiResponse(false, "Pedido no implementado en backend", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return ResponseEntity.status(501).body(new ApiResponse(false, "Obtención de pedidos no implementada", null));
    }
}

