package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Notificacion;
import com.rasengaComics.rasengaComics.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuario(Usuario usuario);
}