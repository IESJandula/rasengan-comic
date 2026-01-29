package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Evento;
import com.rasengaComics.rasengaComics.repositories.EventoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> obtener(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento guardar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }

    public Evento actualizar(Long id, Evento eventoActualizado) {
        Optional<Evento> optEvento = eventoRepository.findById(id);
        if (optEvento.isPresent()) {
            Evento evento = optEvento.get();
            if (eventoActualizado.getNombre() != null) {
                evento.setNombre(eventoActualizado.getNombre());
            }
            if (eventoActualizado.getDescripcion() != null) {
                evento.setDescripcion(eventoActualizado.getDescripcion());
            }
            if (eventoActualizado.getFechaHora() != null) {
                evento.setFechaHora(eventoActualizado.getFechaHora());
            }
            if (eventoActualizado.getUbicacion() != null) {
                evento.setUbicacion(eventoActualizado.getUbicacion());
            }
            if (eventoActualizado.getImagenUrl() != null) {
                evento.setImagenUrl(eventoActualizado.getImagenUrl());
            }
            return eventoRepository.save(evento);
        }
        return null;
    }
}

