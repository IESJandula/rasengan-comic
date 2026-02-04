package com.rasengaComics.rasengaComics.mappers;

import com.rasengaComics.rasengaComics.dto.request.ResenaRequest;
import com.rasengaComics.rasengaComics.dto.response.ResenaResponse;
import com.rasengaComics.rasengaComics.models.Resena;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResenaMapper {

    public ResenaResponse toResponse(Resena resena) {
        if (resena == null) {
            return null;
        }

        ResenaResponse response = new ResenaResponse();
        response.setId(resena.getId());
        response.setUsuarioUid(resena.getUsuario() != null ? resena.getUsuario().getUid() : null);
        response.setProductoId(resena.getProducto() != null ? resena.getProducto().getId() : null);
        response.setEstrellas(resena.getCalificacion());
        response.setComentario(resena.getContenido());
        response.setFecha(resena.getFechaCreacion());

        return response;
    }

    public List<ResenaResponse> toResponseList(List<Resena> resenas) {
        if (resenas == null || resenas.isEmpty()) {
            return new ArrayList<>();
        }

        List<ResenaResponse> result = new ArrayList<>();
        for (Resena resena : resenas) {
            result.add(toResponse(resena));
        }
        return result;
    }

    public Resena toEntity(ResenaRequest request) {
        if (request == null) {
            return null;
        }

        Resena resena = new Resena();
        if (request.getEstrellas() != null) {
            resena.setCalificacion(request.getEstrellas());
        }
        if (request.getComentario() != null) {
            resena.setContenido(request.getComentario());
        }

        return resena;
    }
}

