package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Producto;
import com.rasengaComics.rasengaComics.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public Optional<Producto> obtener(Long id) {
        return repo.findById(id);
    }

    public Producto guardar(Producto p) {
        return repo.save(p);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // Mapping helpers to convert between entity and DTOs
    public static com.rasengaComics.rasengaComics.dto.response.ProductoResponse toResponse(Producto p) {
        if (p == null) return null;
        com.rasengaComics.rasengaComics.dto.response.ProductoResponse r = new com.rasengaComics.rasengaComics.dto.response.ProductoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setDescripcion(p.getDescripcion());
        r.setPrecio(p.getPrecio());
        r.setStock(p.getStock());
        r.setCategoriaId(p.getCategoriaId());
        r.setSubcategoriaId(p.getSubcategoriaId());
        return r;
    }

    public static Producto fromRequest(com.rasengaComics.rasengaComics.dto.request.ProductoRequest req) {
        if (req == null) return null;
        Producto p = new Producto();
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());
        p.setCategoriaId(req.getCategoriaId());
        p.setSubcategoriaId(req.getSubcategoriaId());
        return p;
    }

    public com.rasengaComics.rasengaComics.dto.response.ProductoResponse guardarDesdeRequest(com.rasengaComics.rasengaComics.dto.request.ProductoRequest req) {
        Producto p = fromRequest(req);
        Producto creado = guardar(p);
        return toResponse(creado);
    }
}


