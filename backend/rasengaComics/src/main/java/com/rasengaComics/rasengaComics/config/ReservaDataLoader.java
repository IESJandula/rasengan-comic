package com.rasengaComics.rasengaComics.config;

import com.rasengaComics.rasengaComics.models.Reserva;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.models.Evento;
import com.rasengaComics.rasengaComics.repositories.ReservaRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.rasengaComics.rasengaComics.repositories.EventoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class ReservaDataLoader {

    @Bean
    CommandLineRunner initReservas(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, 
                                    EventoRepository eventoRepository) {
        return args -> {
            // Solo cargar reservas si la base de datos está vacía
            if (reservaRepository.count() == 0) {
                // Esperar a que los datos anteriores estén disponibles
                List<Usuario> usuarios = usuarioRepository.findAll();
                List<Evento> eventos = eventoRepository.findAll();
                
                if (!usuarios.isEmpty() && !eventos.isEmpty()) {
                    List<Reserva> reservas = new ArrayList<>();
                    
                    // Crear reservas asociando usuarios con eventos
                    int userIndex = 0;
                    int eventoIndex = 0;
                    
                    for (int i = 0; i < 8; i++) {
                        Reserva reserva = new Reserva();
                        reserva.setUsuario(usuarios.get(userIndex % usuarios.size()));
                        reserva.setEvento(eventos.get(eventoIndex % eventos.size()));
                        reserva.setFechaReserva(LocalDateTime.now().minusDays((long)(Math.random() * 30)));
                        reserva.setPersonas(2 + (int)(Math.random() * 4)); // 2-5 personas
                        
                        // 6 activas, 2 canceladas
                        if (i < 6) {
                            reserva.setEstado("ACTIVA");
                        } else {
                            reserva.setEstado("CANCELADA");
                        }
                        
                        reservas.add(reserva);
                        userIndex++;
                        if (i % 2 == 1) eventoIndex++;
                    }
                    
                    reservaRepository.saveAll(reservas);
                    System.out.println("✅ " + reservas.size() + " reservas cargadas exitosamente");
                }
            }
        };
    }
}
