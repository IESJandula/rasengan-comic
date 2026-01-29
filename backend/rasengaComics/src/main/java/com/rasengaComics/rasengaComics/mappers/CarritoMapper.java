package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.response.CarritoResponse;
import com.rasengaComics.rasengaComics.models.Carrito;
import com.rasengaComics.rasengaComics.models.ItemCarrito;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CarritoMapper {

    public CarritoResponse toResponse(Carrito carrito) {
        if (carrito == null) {
            return null;
        }

        CarritoResponse response = new CarritoResponse();
        response.setId(carrito.getId());
        response.setUsuarioUid(carrito.getUsuario().getUid());
        response.setItems(mapItems(carrito.getItems()));
        response.setCantidadItems(carrito.getItems() != null ? carrito.getItems().size() : 0);
        response.setTotal(carrito.getTotal());

        return response;
    }

    public List<CarritoResponse> toResponseList(List<Carrito> carritos) {
        if (carritos == null || carritos.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarritoResponse> result = new ArrayList<>();
        for (Carrito carrito : carritos) {
            result.add(toResponse(carrito));
        }
        return result;
    }

    public List<CarritoResponse.CarritoItem> mapItems(List<ItemCarrito> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarritoResponse.CarritoItem> result = new ArrayList<>();
        for (ItemCarrito item : items) {
            CarritoResponse.CarritoItem responseItem = new CarritoResponse.CarritoItem();
            responseItem.setProductoId(item.getProducto().getId());
            responseItem.setNombre(item.getProducto().getNombre());
            responseItem.setPrecio(item.getProducto().getPrecio());
            responseItem.setCantidad(item.getCantidad());
            result.add(responseItem);
        }
        return result;
    }

    public BigDecimal calcularSubtotal(Carrito carrito) {
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : carrito.getItems()) {
            BigDecimal itemTotal = new BigDecimal(item.getProducto().getPrecio())
                    .multiply(new BigDecimal(item.getCantidad()));
            total = total.add(itemTotal);
        }
        return total;
    }
}
