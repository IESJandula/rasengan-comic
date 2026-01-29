package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.Categoria;
import com.rasengaComics.rasengaComics.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // CRUD básico
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtener(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    // Búsqueda específica
    public Optional<Categoria> obtenerPorNombre(String nombre) {
        return categoriaRepository.findAll().stream()
                .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    // Actualizar
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Optional<Categoria> optCategoria = categoriaRepository.findById(id);
        if (optCategoria.isPresent()) {
            Categoria categoria = optCategoria.get();
            if (categoriaActualizada.getNombre() != null) {
                categoria.setNombre(categoriaActualizada.getNombre());
            }
            if (categoriaActualizada.getDescripcion() != null) {
                categoria.setDescripcion(categoriaActualizada.getDescripcion());
            }
            if (categoriaActualizada.getImagenUrl() != null) {
                categoria.setImagenUrl(categoriaActualizada.getImagenUrl());
            }
            return categoriaRepository.save(categoria);
        }
        return null;
    }
}

