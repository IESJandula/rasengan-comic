package com.rasengaComics.rasengaComics.dto.request;

import java.util.List;

public class CarritoRequest {

    private String usuarioUid;
    private List<CarritoItem> items;

    public CarritoRequest() {}

    public String getUsuarioUid() { return usuarioUid; }
    public void setUsuarioUid(String usuarioUid) { this.usuarioUid = usuarioUid; }

    public List<CarritoItem> getItems() { return items; }
    public void setItems(List<CarritoItem> items) { this.items = items; }

    public static class CarritoItem {
        private Long productoId;
        private Integer cantidad;

        public CarritoItem() {}

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}

