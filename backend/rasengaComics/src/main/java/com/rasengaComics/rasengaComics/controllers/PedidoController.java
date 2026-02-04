package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.PedidoService;
import com.rasengaComics.rasengaComics.models.Pedido;
import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.response.PedidoResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest req) {
        try {
            Pedido pedido = pedidoService.crearPedido(req.getUsuarioUid(), req.getItems());
            return ResponseEntity.status(201).body(toResponse(pedido));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.obtener(id);
        if (pedido.isPresent()) {
            return ResponseEntity.ok(toResponse(pedido.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        List<PedidoResponse> respuestas = pedidos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuestas);
    }

    @GetMapping("/usuario/{uid}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable String uid) {
        try {
            // Para este endpoint necesitaríamos inyectar UsuarioService
            // Por ahora devolvemos 501
            return ResponseEntity.status(501).body(new ApiResponse(false, "Endpoint no completamente implementado", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            Pedido actualizado = pedidoService.actualizarEstado(id, nuevoEstado);
            if (actualizado != null) {
                return ResponseEntity.ok(toResponse(actualizado));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    private PedidoResponse toResponse(Pedido p) {
        if (p == null) return null;
        PedidoResponse r = new PedidoResponse();
        r.setId(p.getId());
        r.setUsuarioUid(p.getUsuario().getUid());
        r.setFechaPedido(p.getFechaPedido());
        r.setEstado(p.getEstado());
        r.setCantidadDetalles(p.getDetalles() != null ? p.getDetalles().size() : 0);
        return r;
    }
}

