package com.rasengaComics.rasengaComics.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Long id;
    private String usuarioUid;
    private String estado;
    private Double total;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaCreacion;
    private List<Item> items;
    private Integer cantidadDetalles;

    public PedidoResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuarioUid() { return usuarioUid; }
    public void setUsuarioUid(String usuarioUid) { this.usuarioUid = usuarioUid; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public Integer getCantidadDetalles() { return cantidadDetalles; }
    public void setCantidadDetalles(Integer cantidadDetalles) { this.cantidadDetalles = cantidadDetalles; }

    public static class Item {
        private Long productoId;
        private String nombre;
        private Double precio;
        private Integer cantidad;

        public Item() {}

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}

