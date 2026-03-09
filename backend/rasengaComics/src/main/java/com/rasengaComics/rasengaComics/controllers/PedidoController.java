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

import com.rasengaComics.rasengaComics.entities.Product;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
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
        r.setUsuarioNombre(p.getUsuario().getNombre());
        r.setUsuarioEmail(p.getUsuario().getEmail());
        r.setUsuarioTelefono(p.getUsuario().getTelefono());
        r.setUsuarioCalle(p.getUsuario().getCalle());
        r.setUsuarioCiudad(p.getUsuario().getCiudad());
        r.setUsuarioCodigoPostal(p.getUsuario().getCodigoPostal());
        r.setUsuarioPais(p.getUsuario().getPais());
        r.setFechaPedido(p.getFechaPedido());
        r.setEstado(p.getEstado());
        r.setMetodoEntrega(p.getMetodoEntrega());
        r.setCantidadDetalles(p.getDetalles() != null ? p.getDetalles().size() : 0);
        r.setTotal(calcularTotal(p));
        r.setItems(mapItems(p.getDetalles(), p.getEstado()));
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

    private List<PedidoResponse.Item> mapItems(List<DetallePedido> detalles, String estadoPedido) {
        if (detalles == null) {
            return List.of();
        }
        return detalles.stream().map(detalle -> {
            PedidoResponse.Item item = new PedidoResponse.Item();
            item.setProductoId(detalle.getProducto().getId());
            item.setNombre(detalle.getProducto().getNombre());
            item.setPrecio(detalle.getPrecioUnitario());
            item.setCantidad(detalle.getCantidad());

            Optional<Product> productInfo = productRepository.findById(detalle.getProducto().getId());
            if (productInfo.isEmpty()) {
                productInfo = productRepository.findByName(detalle.getProducto().getNombre());
            }
            
            // Fallback por estado: si ya va por flujo de reservas, no perder el item aunque falle lookup.
            boolean esReservaPorEstado = "PENDIENTE".equalsIgnoreCase(estadoPedido)
                    || "DISPONIBLE".equalsIgnoreCase(estadoPedido)
                    || "RECOGIDO".equalsIgnoreCase(estadoPedido)
                    || "CANCELADO".equalsIgnoreCase(estadoPedido);

            boolean isReserva = productInfo
                    .map(product -> Boolean.TRUE.equals(product.getIsReserve()))
                    .orElse(esReservaPorEstado);
            item.setReserva(isReserva);
            
            // Agregar información adicional del producto si está disponible
            productInfo.ifPresent(product -> {
                item.setImagen(product.getImage());
                item.setCategoria(product.getCategory());
                item.setEditorial("Rasengan Comics");
            });

            if (item.getImagen() == null || item.getImagen().isBlank()) {
                item.setImagen("https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=400");
            }
            if (item.getCategoria() == null || item.getCategoria().isBlank()) {
                item.setCategoria("Producto Reserva");
            }
            if (item.getEditorial() == null || item.getEditorial().isBlank()) {
                item.setEditorial("Rasengan Comics");
            }
            
            return item;
        }).collect(Collectors.toList());
    }
}

