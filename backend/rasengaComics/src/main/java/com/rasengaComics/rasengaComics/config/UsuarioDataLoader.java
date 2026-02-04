package com.rasengaComics.rasengaComics.config;

import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class UsuarioDataLoader {

    @Bean
    CommandLineRunner initUsuarios(UsuarioRepository usuarioRepository) {
        return args -> {
            // Solo cargar usuarios si la base de datos está vacía
            if (usuarioRepository.count() == 0) {
                List<Usuario> usuarios = Arrays.asList(
                    crearUsuario("usuario1", "juan@example.com", "Juan García", "USER"),
                    crearUsuario("usuario2", "maria@example.com", "María López", "USER"),
                    crearUsuario("usuario3", "carlos@example.com", "Carlos Rodríguez", "USER"),
                    crearUsuario("usuario4", "ana@example.com", "Ana Martínez", "USER"),
                    crearUsuario("usuario5", "luis@example.com", "Luis Fernández", "USER"),
                    crearUsuario("admin1", "admin@example.com", "Administrador", "ADMIN")
                );
                usuarioRepository.saveAll(usuarios);
                System.out.println("✅ " + usuarios.size() + " usuarios cargados exitosamente");
            }
        };
    }

    private Usuario crearUsuario(String uid, String email, String nombre, String rol) {
        Usuario usuario = new Usuario();
        usuario.setUid(uid);
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setRol(rol);
        return usuario;
    }
}
