package com.rasengaComics.rasengaComics.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "No token"));
        }
        String token = authHeader.substring(7);
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decoded.getUid();
            Optional<Usuario> opt = usuarioRepository.findById(uid);
            if (opt.isEmpty()) {
                Usuario u = new Usuario();
                u.setUid(uid);
                u.setEmail(decoded.getEmail());
                u.setNombre(decoded.getName() != null ? decoded.getName() : decoded.getEmail());
                u.setRol("USER");
                usuarioRepository.save(u);
                return ResponseEntity.ok(u);
            } else {
                return ResponseEntity.ok(opt.get());
            }
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
}

