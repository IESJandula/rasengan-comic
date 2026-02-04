package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Reserva;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.ReservaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> obtener(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> obtenerPorUsuario(Usuario usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

    public List<Reserva> listarPorUsuarioEmail(String email) {
        return reservaRepository.findByUsuarioEmail(email);
    }

    public Reserva actualizarEstado(Long id, String nuevoEstado) {
        Optional<Reserva> opt = reservaRepository.findById(id);
        if (opt.isPresent()) {
            Reserva r = opt.get();
            r.setEstado(nuevoEstado);
            return reservaRepository.save(r);
        }
        return null;
    }

    // Contar total de reservas
    public long countAll() {
        return reservaRepository.count();
    }

    // Contar reservas activas
    public long countActivas() {
        return reservaRepository.findAll().stream()
                .filter(r -> "ACTIVA".equals(r.getEstado()))
                .count();
    }
}

