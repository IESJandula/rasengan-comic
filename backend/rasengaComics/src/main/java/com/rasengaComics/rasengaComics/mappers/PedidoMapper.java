package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.response.PedidoResponse;
import com.rasengaComics.rasengaComics.models.Pedido;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setUsuarioUid(pedido.getUsuario() != null ? pedido.getUsuario().getUid() : null);
        response.setFechaCreacion(pedido.getFechaPedido());
        response.setEstado(pedido.getEstado());

        return response;
    }

    public List<PedidoResponse> toResponseList(List<Pedido> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) {
            return new ArrayList<>();
        }

        List<PedidoResponse> result = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            result.add(toResponse(pedido));
        }
        return result;
    }

    public Pedido toEntity(PedidoRequest request) {
        if (request == null) {
            return null;
        }

        Pedido pedido = new Pedido();
        // Los campos ignorados se dejan null o por defecto
        return pedido;
    }
}


