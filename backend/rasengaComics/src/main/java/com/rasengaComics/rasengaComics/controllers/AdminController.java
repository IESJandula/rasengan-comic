package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.UsuarioService;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            return ResponseEntity.ok(usuarioService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "No autorizado", null));
        }
    }

    @PutMapping("/usuarios/{uid}/rol")
    public ResponseEntity<?> cambiarRolUsuario(
            @PathVariable String uid,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String nuevoRol = body.get("rol");
            var usuario = usuarioService.cambiarRol(uid, nuevoRol);
            if (usuario != null) {
                return ResponseEntity.ok(new ApiResponse(true, "Rol actualizado", usuarioService.toResponse(usuario)));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "No autorizado", null));
        }
    }

    @DeleteMapping("/usuarios/{uid}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable String uid,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            usuarioService.eliminar(uid);
            return ResponseEntity.ok(new ApiResponse(true, "Usuario eliminado", null));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "No autorizado", null));
        }
    }
}

