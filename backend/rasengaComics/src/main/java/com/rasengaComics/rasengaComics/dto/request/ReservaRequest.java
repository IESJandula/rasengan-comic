package com.rasengaComics.rasengaComics.dto.request;

import java.time.LocalDateTime;

public class ReservaRequest {

    private String usuarioUid;
    private Long eventoId;
    private Integer personas;
    private LocalDateTime fechaReserva;

    public ReservaRequest() {}

    public String getUsuarioUid() { return usuarioUid; }
    public void setUsuarioUid(String usuarioUid) { this.usuarioUid = usuarioUid; }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public Integer getPersonas() { return personas; }
    public void setPersonas(Integer personas) { this.personas = personas; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
}

