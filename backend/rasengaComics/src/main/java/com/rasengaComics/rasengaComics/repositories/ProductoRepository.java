package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}


