package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.UsuarioService;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.dto.response.UsuarioResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Obtener usuario por UID
    @GetMapping("/{uid}")
    public ResponseEntity<?> obtener(@PathVariable String uid) {
        Optional<Usuario> usuario = usuarioService.obtenerPorUid(uid);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuarioService.toResponse(usuario.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // Listar todos los usuarios (solo admin)
    @GetMapping
    public ResponseEntity<?> listar(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Validación simple - idealmente verificar token
        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // Actualizar perfil de usuario
    @PutMapping("/{uid}")
    public ResponseEntity<?> actualizar(
            @PathVariable String uid,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String nombre = body.get("nombre");
            String email = body.get("email");
            Usuario actualizado = usuarioService.actualizarPerfil(uid, nombre, email);
            if (actualizado != null) {
                return ResponseEntity.ok(usuarioService.toResponse(actualizado));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Cambiar rol de usuario (solo admin)
    @PutMapping("/{uid}/rol")
    public ResponseEntity<?> cambiarRol(
            @PathVariable String uid,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String nuevoRol = body.get("rol");
            Usuario actualizado = usuarioService.cambiarRol(uid, nuevoRol);
            if (actualizado != null) {
                return ResponseEntity.ok(usuarioService.toResponse(actualizado));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Eliminar usuario
    @DeleteMapping("/{uid}")
    public ResponseEntity<?> eliminar(
            @PathVariable String uid,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Optional<Usuario> usuario = usuarioService.obtenerPorUid(uid);
            if (usuario.isPresent()) {
                usuarioService.eliminar(uid);
                return ResponseEntity.ok(new ApiResponse(true, "Usuario eliminado", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

