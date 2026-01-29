package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}

