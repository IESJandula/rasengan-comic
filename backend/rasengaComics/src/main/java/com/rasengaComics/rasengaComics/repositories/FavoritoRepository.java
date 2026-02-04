package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Favorito;
import com.rasengaComics.rasengaComics.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuario(Usuario usuario);
    void deleteByUsuarioAndProductoId(Usuario usuario, Long productoId);
}