package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}

