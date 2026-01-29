package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Carrito;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.models.Producto;
import com.rasengaComics.rasengaComics.repositories.CarritoRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.rasengaComics.rasengaComics.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository,
                        UsuarioRepository usuarioRepository,
                        ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    // Obtener o crear carrito para un usuario
    public Carrito obtenerOCrearCarrito(String usuarioUid) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioUid);
        if (optUsuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = optUsuario.get();
        Optional<Carrito> optCarrito = carritoRepository.findByUsuario(usuario);
        
        if (optCarrito.isPresent()) {
            return optCarrito.get();
        } else {
            Carrito nuevoCarrito = new Carrito(usuario);
            return carritoRepository.save(nuevoCarrito);
        }
    }

    // Guardar carrito
    public Carrito guardar(Carrito carrito) {
        carrito.setFechaActualizacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    // Vaciar carrito
    public void vaciarCarrito(Long carritoId) {
        Optional<Carrito> optCarrito = carritoRepository.findById(carritoId);
        if (optCarrito.isPresent()) {
            Carrito carrito = optCarrito.get();
            carrito.getItems().clear();
            carrito.setTotal(0.0);
            carritoRepository.save(carrito);
        }
    }

    // Eliminar carrito
    public void eliminar(Long carritoId) {
        carritoRepository.deleteById(carritoId);
    }

    // Calcular total del carrito
    public Double calcularTotal(Carrito carrito) {
        Double total = 0.0;
        if (carrito.getItems() != null) {
            total = carrito.getItems().stream()
                    .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                    .sum();
        }
        return total;
    }
}

