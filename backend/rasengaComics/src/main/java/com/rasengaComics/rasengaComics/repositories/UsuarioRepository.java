package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}

