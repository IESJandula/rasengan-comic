package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Pedido;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.models.Producto;
import com.rasengaComics.rasengaComics.models.DetallePedido;
import com.rasengaComics.rasengaComics.repositories.PedidoRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
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
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, 
                        UsuarioRepository usuarioRepository,
                        ProductoRepository productoRepository,
                        DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
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

