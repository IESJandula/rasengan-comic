package com.rasengaComics.rasengaComics.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_descuento")
public class CodigoDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private Double porcentaje;
    private Integer cantidadUsos;
    private Integer usosRestantes;
    private LocalDateTime fechaVencimiento;
    private Boolean activo;

    public CodigoDescuento() {}

    public CodigoDescuento(String codigo, Double porcentaje, Integer cantidadUsos, LocalDateTime fechaVencimiento) {
        this.codigo = codigo;
        this.porcentaje = porcentaje;
        this.cantidadUsos = cantidadUsos;
        this.usosRestantes = cantidadUsos;
        this.fechaVencimiento = fechaVencimiento;
        this.activo = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Integer getCantidadUsos() {
        return cantidadUsos;
    }

    public void setCantidadUsos(Integer cantidadUsos) {
        this.cantidadUsos = cantidadUsos;
    }

    public Integer getUsosRestantes() {
        return usosRestantes;
    }

    public void setUsosRestantes(Integer usosRestantes) {
        this.usosRestantes = usosRestantes;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
