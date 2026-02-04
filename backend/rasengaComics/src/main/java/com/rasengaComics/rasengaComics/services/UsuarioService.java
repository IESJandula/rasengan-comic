package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.rasengaComics.rasengaComics.dto.response.UsuarioResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CRUD básico
    public Optional<Usuario> obtenerPorUid(String uid) {
        return usuarioRepository.findById(uid);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminar(String uid) {
        usuarioRepository.deleteById(uid);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Búsqueda específica
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
                .findFirst();
    }

    // Actualizar perfil
    public Usuario actualizarPerfil(String uid, String nombre, String email) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(uid);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            if (nombre != null && !nombre.isEmpty()) {
                usuario.setNombre(nombre);
            }
            if (email != null && !email.isEmpty()) {
                usuario.setEmail(email);
            }
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // Cambiar rol (solo admin)
    public Usuario cambiarRol(String uid, String nuevoRol) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(uid);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuario.setRol(nuevoRol);
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // Verificar si es admin
    public boolean esAdmin(String uid) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(uid);
        return optUsuario.isPresent() && "ADMIN".equals(optUsuario.get().getRol());
    }

    // Mapping helpers
    public UsuarioResponse toResponse(Usuario u) {
        if (u == null) return null;
        UsuarioResponse r = new UsuarioResponse();
        r.setUid(u.getUid());
        r.setEmail(u.getEmail());
        r.setNombre(u.getNombre());
        r.setRol(u.getRol());
        return r;
    }
}

