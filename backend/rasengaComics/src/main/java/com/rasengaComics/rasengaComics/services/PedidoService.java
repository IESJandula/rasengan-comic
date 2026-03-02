package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.entities.Product;
import com.rasengaComics.rasengaComics.models.Pedido;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.models.Producto;
import com.rasengaComics.rasengaComics.models.DetallePedido;
import com.rasengaComics.rasengaComics.repositories.PedidoRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import com.rasengaComics.rasengaComics.repositories.ProductoRepository;
import com.rasengaComics.rasengaComics.repositories.DetallePedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductRepository productRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, 
                        UsuarioRepository usuarioRepository,
                        ProductRepository productRepository,
                        ProductoRepository productoRepository,
                        DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productRepository = productRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // CRUD básico
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtener(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    // Obtener pedidos de un usuario
    public List<Pedido> obtenerPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    // Crear pedido con detalles
    public Pedido crearPedido(String usuarioUid, List<com.rasengaComics.rasengaComics.dto.request.PedidoRequest.Item> items) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioUid);
        if (optUsuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = optUsuario.get();
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Agregar detalles
        for (com.rasengaComics.rasengaComics.dto.request.PedidoRequest.Item item : items) {
            Optional<Producto> optProducto = productoRepository.findById(item.getProductoId());
            if (optProducto.isPresent()) {
                Producto producto = optProducto.get();
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoGuardado);
                detalle.setProducto(producto);
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecio());
                detallePedidoRepository.save(detalle);
            }
        }

        return pedidoGuardado;
    }

    public Pedido crearPedidoPagado(String usuarioUid,
                                    List<com.rasengaComics.rasengaComics.dto.request.PedidoRequest.Item> items,
                                    String stripeSessionId,
                                    String stripePaymentIntentId) {
        Optional<Pedido> existente = pedidoRepository.findByStripeSessionId(stripeSessionId);
        if (existente.isPresent()) {
            return existente.get();
        }

        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioUid);
        if (optUsuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = optUsuario.get();
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PAGADO");
        pedido.setStripeSessionId(stripeSessionId);
        pedido.setStripePaymentIntentId(stripePaymentIntentId);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        double total = 0.0;

        for (com.rasengaComics.rasengaComics.dto.request.PedidoRequest.Item item : items) {
            // Buscar en la tabla products (no productos)
            Optional<Product> optProduct = productRepository.findById(item.getProductoId());
            if (optProduct.isPresent()) {
                Product product = optProduct.get();
                
                // Buscar o crear el producto correspondiente en la tabla productos
                Optional<Producto> optProducto = productoRepository.findById(item.getProductoId());
                Producto producto;
                if (optProducto.isPresent()) {
                    producto = optProducto.get();
                } else {
                    // Crear producto en tabla productos desde product si no existe
                    producto = new Producto();
                    producto.setId(product.getId());
                    producto.setNombre(product.getName());
                    producto.setDescripcion(product.getCategory() + (product.getSubcategory() != null ? " - " + product.getSubcategory() : ""));
                    producto.setPrecio(product.getPrice());
                    producto.setStock(100); // Stock por defecto
                    producto = productoRepository.save(producto);
                }
                
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoGuardado);
                detalle.setProducto(producto);
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(product.getPrice());
                detallePedidoRepository.save(detalle);
                total += product.getPrice() * item.getCantidad();
            }
        }

        pedidoGuardado.setTotal(total);
        return pedidoRepository.save(pedidoGuardado);
    }

    // Actualizar estado
    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Optional<Pedido> optPedido = pedidoRepository.findById(id);
        if (optPedido.isPresent()) {
            Pedido pedido = optPedido.get();
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        }
        return null;
    }
}

