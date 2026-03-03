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
                    crearEvento("Torneo TCG Magic: The Gathering", 
                                "Competición oficial de Magic con premios para los 3 primeros puestos. Formato Standard. Inscripción limitada a 32 jugadores.", 
                                LocalDateTime.of(2026, 2, 15, 18, 0), "Sala Principal", "https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=400"),
                    crearEvento("Taller de Dibujo Manga", 
                                "Aprende las técnicas básicas del dibujo manga con un ilustrador profesional. Materiales incluidos. Todos los niveles bienvenidos.", 
                                LocalDateTime.of(2026, 2, 22, 17, 0), "Aula de Arte", "https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=400"),
                    crearEvento("Noche de Board Games", 
                                "Velada de juegos de mesa con nuevos lanzamientos. Trae a tus amigos y descubre nuevos juegos. Torneos de Catan y Carcassonne.", 
                                LocalDateTime.of(2026, 2, 18, 19, 0), "Zona Gaming", "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=400"),
                    crearEvento("Meet & Greet con Autores de Manga", 
                                "Conoce a autores locales de manga, firma de ejemplares y sesión de preguntas. Edición limitada disponible para compra.", 
                                LocalDateTime.of(2026, 2, 28, 16, 0), "Entrada Principal", "https://images.unsplash.com/photo-1618519764620-7403abdbdfe9?w=400"),
                    crearEvento("Campeonato Pokémon TCG", 
                                "Torneo oficial de Pokémon Trading Card Game. Formato Expanded. Premios: sobres, cartas promocionales y trofeos.", 
                                LocalDateTime.of(2026, 3, 2, 15, 0), "Sala Principal", "https://images.unsplash.com/photo-1606503153255-59d7a5e5a1b5?w=400"),
                    crearEvento("Screening Anime: Nuevos Estrenos", 
                                "Proyección de los últimos episodios de animes populares en pantalla grande. Palomitas y bebidas incluidas.", 
                                LocalDateTime.of(2026, 2, 20, 20, 0), "Auditorio", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400"),
                    crearEvento("Torneo Yu-Gi-Oh! Duel Masters", 
                                "Competición de Yu-Gi-Oh con formato avanzado. Inscripción el mismo día. Premios para los 8 mejores.", 
                                LocalDateTime.of(2026, 3, 8, 17, 0), "Sala de Torneos", "https://images.unsplash.com/photo-1628868445294-d0f4e2dd0ff5?w=400"),
                    crearEvento("Workshop: Creación de Personajes", 
                                "Taller de diseño y creación de personajes para cómics y manga. Técnicas de personalidad, diseño visual y storytelling.", 
                                LocalDateTime.of(2026, 3, 5, 18, 30), "Aula Creativa", "https://images.unsplash.com/photo-1609743522653-52354461eb27?w=400"),
                    crearEvento("Feria de Intercambio de Cartas", 
                                "Trae tus cartas duplicadas e intercámbialas con otros coleccionistas. Magic, Pokémon, Yu-Gi-Oh y más.", 
                                LocalDateTime.of(2026, 2, 25, 16, 0), "Zona de Intercambios", "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400"),
                    crearEvento("Maratón de One Piece", 
                                "Maratón de episodios especiales de One Piece. 6 horas de aventura con Luffy y la tripulación. Merchandising exclusivo.", 
                                LocalDateTime.of(2026, 3, 10, 14, 0), "Auditorio", "https://images.unsplash.com/photo-1635863138275-d9b33299680b?w=400"),
                    crearEvento("Torneo Dragon Ball Super Card Game", 
                                "Competición oficial de Dragon Ball Super TCG. Formato estándar. Premios exclusivos de la serie.", 
                                LocalDateTime.of(2026, 3, 12, 16, 0), "Sala Principal", "https://images.unsplash.com/photo-1643714332478-89616af39d39?w=400"),
                    crearEvento("Cosplay Contest 2026", 
                                "Concurso de cosplay con premios en múltiples categorías. Jurado profesional. Inscripciones hasta el día del evento.", 
                                LocalDateTime.of(2026, 3, 15, 18, 0), "Escenario Principal", "https://images.unsplash.com/photo-1609743522653-52354461eb27?w=400")
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
