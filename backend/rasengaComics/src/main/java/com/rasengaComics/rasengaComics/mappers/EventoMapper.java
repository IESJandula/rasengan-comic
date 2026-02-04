package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.request.EventoRequest;
import com.rasengaComics.rasengaComics.dto.response.EventoResponse;
import com.rasengaComics.rasengaComics.models.Evento;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventoMapper {

    public EventoResponse toResponse(Evento evento) {
        if (evento == null) {
            return null;
        }

        EventoResponse response = new EventoResponse();
        response.setId(evento.getId());
        response.setTitulo(evento.getNombre());
        response.setDescripcion(evento.getDescripcion());
        response.setFecha(evento.getFechaHora());
        response.setLugar(evento.getUbicacion());
        response.setCapacidad(0); // No existe en modelo

        return response;
    }

    public List<EventoResponse> toResponseList(List<Evento> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return new ArrayList<>();
        }

        List<EventoResponse> result = new ArrayList<>();
        for (Evento evento : eventos) {
            result.add(toResponse(evento));
        }
        return result;
    }

    public Evento toEntity(EventoRequest request) {
        if (request == null) {
            return null;
        }

        Evento evento = new Evento();
        evento.setNombre(request.getTitulo());
        evento.setDescripcion(request.getDescripcion());
        evento.setFechaHora(request.getFecha());
        evento.setUbicacion(request.getLugar());

        return evento;
    }

    public void updateFromRequest(EventoRequest request, Evento evento) {
        if (request == null || evento == null) {
            return;
        }

        if (request.getTitulo() != null) {
            evento.setNombre(request.getTitulo());
        }
        if (request.getDescripcion() != null) {
            evento.setDescripcion(request.getDescripcion());
        }
        if (request.getFecha() != null) {
            evento.setFechaHora(request.getFecha());
        }
        if (request.getLugar() != null) {
            evento.setUbicacion(request.getLugar());
        }
    }
}


