package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}

