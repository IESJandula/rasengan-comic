package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.request.RegisterRequest;
import com.rasengaComics.rasengaComics.dto.response.UsuarioResponse;
import com.rasengaComics.rasengaComics.models.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponse response = new UsuarioResponse();
        response.setUid(usuario.getUid());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setRol(usuario.getRol());

        return response;
    }

    public Usuario toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setRol("USER");

        return usuario;
    }

    public void updateFromRequest(RegisterRequest request, Usuario usuario) {
        if (request == null || usuario == null) {
            return;
        }

        if (request.getEmail() != null) {
            usuario.setEmail(request.getEmail());
        }
        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
    }
}

