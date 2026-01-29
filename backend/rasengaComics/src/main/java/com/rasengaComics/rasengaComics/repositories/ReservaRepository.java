package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Reserva;
import com.rasengaComics.rasengaComics.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);
}

