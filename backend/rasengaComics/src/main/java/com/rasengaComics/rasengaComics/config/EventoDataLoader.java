package com.rasengaComics.rasengaComics.config;

import com.rasengaComics.rasengaComics.models.Evento;
import com.rasengaComics.rasengaComics.repositories.EventoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class EventoDataLoader {

    @Bean
    CommandLineRunner initEventos(EventoRepository eventoRepository) {
        return args -> {
            // Solo cargar eventos si la base de datos está vacía
            if (eventoRepository.count() == 0) {
                List<Evento> eventos = Arrays.asList(
                    crearEvento("Rasengan Comic Con 2024", "Una de las convenciones más grandes del año con cosplay, ventas y actividades", 
                                LocalDateTime.of(2024, 6, 15, 10, 0), "Madrid, España", "evento-comic-con.jpg"),
                    crearEvento("Torneo de Magic: The Gathering", "Competencia oficial de Magic con premios emocionantes", 
                                LocalDateTime.of(2024, 5, 20, 14, 0), "Barcelona, España", "evento-magic.jpg"),
                    crearEvento("Presentación Manga Shonen Jump", "Conoce a los autores y editores de las series más populares", 
                                LocalDateTime.of(2024, 7, 10, 16, 0), "Valencia, España", "evento-manga.jpg"),
                    crearEvento("Exhibición de Figuras Coleccionables", "Exposición de figuras raras y limitadas de todo el mundo", 
                                LocalDateTime.of(2024, 8, 5, 11, 0), "Bilbao, España", "evento-figuras.jpg"),
                    crearEvento("Torneo de Pokémon", "Competencia regional de Pokémon TCG con clasificaciones al mundial", 
                                LocalDateTime.of(2024, 9, 12, 10, 0), "Sevilla, España", "evento-pokemon.jpg")
                );
                eventoRepository.saveAll(eventos);
                System.out.println("✅ " + eventos.size() + " eventos cargados exitosamente");
            }
        };
    }

    private Evento crearEvento(String nombre, String descripcion, LocalDateTime fechaHora, 
                                String ubicacion, String imagenUrl) {
        Evento evento = new Evento();
        evento.setNombre(nombre);
        evento.setDescripcion(descripcion);
        evento.setFechaHora(fechaHora);
        evento.setUbicacion(ubicacion);
        evento.setImagenUrl(imagenUrl);
        return evento;
    }
}
