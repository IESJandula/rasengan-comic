package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Favorito;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.models.Producto;
import com.rasengaComics.rasengaComics.repositories.FavoritoRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.rasengaComics.rasengaComics.repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    public List<Favorito> listarTodos() {
        return favoritoRepository.findAll();
    }

    public Optional<Favorito> obtener(Long id) {
        return favoritoRepository.findById(id);
    }

    public Favorito guardar(Favorito favorito) {
        return favoritoRepository.save(favorito);
    }

    public void eliminar(Long id) {
        favoritoRepository.deleteById(id);
    }

    public List<Favorito> obtenerPorUsuario(Usuario usuario) {
        return favoritoRepository.findByUsuario(usuario);
    }

    public void agregarFavorito(String usuarioUid, Long productoId) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioUid);
        Optional<Producto> optProducto = productoRepository.findById(productoId);

        if (optUsuario.isPresent() && optProducto.isPresent()) {
            Favorito favorito = new Favorito(optUsuario.get(), optProducto.get());
            favoritoRepository.save(favorito);
        }
    }

    public void removerFavorito(String usuarioUid, Long productoId) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioUid);
        if (optUsuario.isPresent()) {
            favoritoRepository.deleteByUsuarioAndProductoId(optUsuario.get(), productoId);
        }
    }
}

