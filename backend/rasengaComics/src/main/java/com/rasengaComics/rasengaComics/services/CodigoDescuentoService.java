package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.CodigoDescuento;
import com.rasengaComics.rasengaComics.repositories.CodigoDescuentoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CodigoDescuentoService {

    private final CodigoDescuentoRepository codigoRepository;

    public CodigoDescuentoService(CodigoDescuentoRepository codigoRepository) {
        this.codigoRepository = codigoRepository;
    }

    public List<CodigoDescuento> listarTodos() {
        return codigoRepository.findAll();
    }

    public Optional<CodigoDescuento> obtener(Long id) {
        return codigoRepository.findById(id);
    }

    public CodigoDescuento guardar(CodigoDescuento codigo) {
        return codigoRepository.save(codigo);
    }

    public void eliminar(Long id) {
        codigoRepository.deleteById(id);
    }

    public Optional<CodigoDescuento> obtenerPorCodigo(String codigo) {
        return codigoRepository.findByCodigo(codigo);
    }

    public boolean validarCodigo(String codigo) {
        Optional<CodigoDescuento> optCodigo = codigoRepository.findByCodigo(codigo);
        if (optCodigo.isEmpty()) return false;
        CodigoDescuento cd = optCodigo.get();
        return cd.getActivo() && cd.getUsosRestantes() > 0;
    }
}

