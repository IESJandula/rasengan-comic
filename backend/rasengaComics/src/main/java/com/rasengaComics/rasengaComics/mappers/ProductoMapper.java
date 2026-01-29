package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.request.ProductoRequest;
import com.rasengaComics.rasengaComics.dto.response.ProductoResponse;
import com.rasengaComics.rasengaComics.models.Producto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductoMapper {

    public ProductoResponse toResponse(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        response.setCategoriaId(producto.getCategoriaId());
        response.setSubcategoriaId(producto.getSubcategoriaId());

        return response;
    }

    public List<ProductoResponse> toResponseList(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductoResponse> result = new ArrayList<>();
        for (Producto producto : productos) {
            result.add(toResponse(producto));
        }
        return result;
    }

    public Producto toEntity(ProductoRequest request) {
        if (request == null) {
            return null;
        }

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setSubcategoriaId(request.getSubcategoriaId());

        return producto;
    }

    public void updateFromRequest(ProductoRequest request, Producto producto) {
        if (request == null || producto == null) {
            return;
        }

        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            producto.setPrecio(request.getPrecio());
        }
        if (request.getStock() != null) {
            producto.setStock(request.getStock());
        }
        if (request.getCategoriaId() != null) {
            producto.setCategoriaId(request.getCategoriaId());
        }
        if (request.getSubcategoriaId() != null) {
            producto.setSubcategoriaId(request.getSubcategoriaId());
        }
    }
}

