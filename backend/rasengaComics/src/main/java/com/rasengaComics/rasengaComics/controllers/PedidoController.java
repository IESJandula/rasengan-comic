package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.PedidoService;
import com.rasengaComics.rasengaComics.models.Pedido;
import com.rasengaComics.rasengaComics.models.DetallePedido;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.response.PedidoResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final ProductRepository productRepository;

    public PedidoController(PedidoService pedidoService,
                           UsuarioRepository usuarioRepository,
                           ProductRepository productRepository) {
        this.pedidoService = pedidoService;
        this.usuarioRepository = usuarioRepository;
        this.productRepository = productRepository;
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
            Usuario usuario = usuarioRepository.findById(uid).orElse(null);
            if (usuario == null) {
                return ResponseEntity.ok(List.of());
            }

            List<Pedido> pedidos = pedidoService.obtenerPorUsuario(usuario);
            List<PedidoResponse> respuestas = pedidos.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(respuestas);
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
        r.setTotal(calcularTotal(p));
        r.setItems(mapItems(p.getDetalles()));
        return r;
    }

    private Double calcularTotal(Pedido pedido) {
        if (pedido.getTotal() != null) {
            return pedido.getTotal();
        }
        if (pedido.getDetalles() == null) {
            return 0.0;
        }
        return pedido.getDetalles().stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                .sum();
    }

    private List<PedidoResponse.Item> mapItems(List<DetallePedido> detalles) {
        if (detalles == null) {
            return List.of();
        }
        return detalles.stream().map(detalle -> {
            PedidoResponse.Item item = new PedidoResponse.Item();
            item.setProductoId(detalle.getProducto().getId());
            item.setNombre(detalle.getProducto().getNombre());
            item.setPrecio(detalle.getPrecioUnitario());
            item.setCantidad(detalle.getCantidad());
            boolean isReserva = productRepository.findById(detalle.getProducto().getId())
                    .map(product -> Boolean.TRUE.equals(product.getIsReserve()))
                    .orElse(false);
            item.setReserva(isReserva);
            return item;
        }).collect(Collectors.toList());
    }
}

