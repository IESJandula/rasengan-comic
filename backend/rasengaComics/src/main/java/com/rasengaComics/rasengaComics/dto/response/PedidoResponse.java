package com.rasengaComics.rasengaComics.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Long id;
    private String usuarioUid;
    private String usuarioNombre;
    private String usuarioEmail;
    private String usuarioTelefono;
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

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }

    public String getUsuarioTelefono() { return usuarioTelefono; }
    public void setUsuarioTelefono(String usuarioTelefono) { this.usuarioTelefono = usuarioTelefono; }

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
        private Boolean reserva;
        private String imagen;
        private String categoria;
        private String editorial;

        public Item() {}

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public Boolean getReserva() { return reserva; }
        public void setReserva(Boolean reserva) { this.reserva = reserva; }

        public String getImagen() { return imagen; }
        public void setImagen(String imagen) { this.imagen = imagen; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public String getEditorial() { return editorial; }
        public void setEditorial(String editorial) { this.editorial = editorial; }
    }
}

