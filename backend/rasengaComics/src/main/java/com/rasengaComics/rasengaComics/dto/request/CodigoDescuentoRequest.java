package com.rasengaComics.rasengaComics.dto.request;

import java.time.LocalDate;

public class CodigoDescuentoRequest {
    private String codigo;
    private Double descuentoPercent;
    private LocalDate fechaExpiracion;

    public CodigoDescuentoRequest() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Double getDescuentoPercent() { return descuentoPercent; }
    public void setDescuentoPercent(Double descuentoPercent) { this.descuentoPercent = descuentoPercent; }

    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDate fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}

