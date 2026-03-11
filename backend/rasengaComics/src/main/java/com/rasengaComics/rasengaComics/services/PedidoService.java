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
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
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

    @Transactional
    public Pedido crearPedidoPagado(String usuarioUid,
                                    List<com.rasengaComics.rasengaComics.dto.request.PedidoRequest.Item> items,
                                    String metodoEntrega,
                                    String stripeSessionId,
                                    String stripePaymentIntentId) {
        logger.info("【CREAR PEDIDO PAGADO】 usuarioUid: {}, items: {}, sessionId: {}", usuarioUid, items.size(), stripeSessionId);
        
        Optional<Pedido> existente = pedidoRepository.findByStripeSessionId(stripeSessionId);
        if (existente.isPresent()) {
            logger.warn("【PEDIDO DUPLICADO】 Ya existe pedido con sessionId: {}", stripeSessionId);
            return existente.get();
        }

        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioUid);
        if (optUsuario.isEmpty()) {
            logger.error("【ERROR】 Usuario no encontrado: {}", usuarioUid);
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = optUsuario.get();
        logger.info("【USUARIO ENCONTRADO】 {} ({})", usuario.getNombre(), usuarioUid);
        
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PAGADO");
        pedido.setMetodoEntrega(normalizarMetodoEntrega(metodoEntrega));
        pedido.setStripeSessionId(stripeSessionId);
        pedido.setStripePaymentIntentId(stripePaymentIntentId);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        logger.info("【PEDIDO GUARDADO】 ID: {}", pedidoGuardado.getId());
        
        double total = 0.0;

        for (com.rasengaComics.rasengaComics.dto.request.PedidoRequest.Item item : items) {
            logger.info("【PROCESANDO ITEM】 ProductoId: {}, Cantidad: {}", item.getProductoId(), item.getCantidad());
            
            // Buscar en la tabla products (ÚNICA tabla de productos)
            Optional<Product> optProduct = productRepository.findById(item.getProductoId());
            if (optProduct.isPresent()) {
                Product product = optProduct.get();
                logger.info("【PRODUCTO ENCONTRADO】 {}", product.getName());

                Integer cantidad = item.getCantidad();
                if (cantidad == null || cantidad <= 0) {
                    logger.error("【ERROR】 Cantidad inválida: {}", cantidad);
                    throw new IllegalArgumentException("Cantidad inválida para el producto: " + item.getProductoId());
                }

                int stockActual = product.getStock() != null ? product.getStock() : 0;
                logger.info("【STOCK ACTUAL】 {}, Solicitado: {}", stockActual, cantidad);
                
                if (stockActual < cantidad) {
                    logger.error("【ERROR】 Stock insuficiente");
                    throw new IllegalArgumentException(
                            "Stock insuficiente para el producto " + product.getName() +
                            ". Disponible: " + stockActual + ", solicitado: " + cantidad
                    );
                }

                product.setStock(stockActual - cantidad);
                productRepository.save(product);
                logger.info("【STOCK ACTUALIZADO】 Nuevo stock: {}", product.getStock());
                
                // NO usar item.getProductoId() sobre tabla productos: ese ID pertenece a tabla products
                // y puede cruzarse con registros distintos. Vincular por nombre evita asociaciones erróneas.
                Optional<Producto> optProducto = productoRepository.findByNombre(product.getName());
                Producto producto;
                if (optProducto.isPresent()) {
                    producto = optProducto.get();
                    logger.info("【PRODUCTO EXISTE EN PRODUCTOS】 ID: {}, Nombre: {}", producto.getId(), producto.getNombre());
                } else {
                    // Crear producto en tabla productos desde products si no existe.
                    // Dejar que la PK sea autogenerada evita conflictos entre IDs de tablas distintas.
                    producto = new Producto();
                    producto.setNombre(product.getName());
                    producto.setDescripcion(product.getCategory() + (product.getSubcategory() != null ? " - " + product.getSubcategory() : ""));
                    producto.setPrecio(product.getPrice());
                    producto.setStock(product.getStock());
                    producto = productoRepository.save(producto);
                    logger.info("【PRODUCTO CREADO EN TABLA productos】 ID: {}, Nombre: {}", producto.getId(), producto.getNombre());
                }
                
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoGuardado);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(product.getPrice());
                detallePedidoRepository.save(detalle);
                logger.info("【DETALLE PEDIDO GUARDADO】");
                
                total += product.getPrice() * cantidad;
            } else {
                logger.warn("【ADVERTENCIA】 Producto no encontrado: {}", item.getProductoId());
            }
        }

        pedidoGuardado.setTotal(total);
        Pedido resultado = pedidoRepository.save(pedidoGuardado);
        logger.info("【PEDIDO FINALIZADO】 ID: {}, Total: {}", resultado.getId(), total);
        return resultado;
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

    private String normalizarMetodoEntrega(String metodoEntrega) {
        if (metodoEntrega == null) {
            return "envio";
        }
        String normalizado = metodoEntrega.trim().toLowerCase();
        if ("tienda".equals(normalizado)) {
            return "tienda";
        }
        return "envio";
    }
}

