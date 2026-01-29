package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.request.ReservaRequest;
import com.rasengaComics.rasengaComics.dto.response.ReservaResponse;
import com.rasengaComics.rasengaComics.models.Reserva;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservaMapper {

    public ReservaResponse toResponse(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        ReservaResponse response = new ReservaResponse();
        response.setId(reserva.getId());
        response.setUsuarioUid(reserva.getUsuario() != null ? reserva.getUsuario().getUid() : null);
        response.setEventoId(reserva.getEvento() != null ? reserva.getEvento().getId() : null);
        response.setPersonas(reserva.getPersonas());
        response.setFechaReserva(reserva.getFechaReserva());
        response.setEstado(reserva.getEstado());

        return response;
    }

    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        if (reservas == null || reservas.isEmpty()) {
            return new ArrayList<>();
        }

        List<ReservaResponse> result = new ArrayList<>();
        for (Reserva reserva : reservas) {
            result.add(toResponse(reserva));
        }
        return result;
    }

    public Reserva toEntity(ReservaRequest request) {
        if (request == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setPersonas(request.getPersonas());
        reserva.setFechaReserva(request.getFechaReserva());
        reserva.setEstado("PENDIENTE");

        return reserva;
    }
}

