package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.models.CodigoDescuento;
import com.rasengaComics.rasengaComics.repositories.CodigoDescuentoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
        return codigoRepository.findByCode(codigo);
    }

    public boolean validarCodigo(String codigo) {
        Optional<CodigoDescuento> optCodigo = codigoRepository.findByCode(codigo);
        if (optCodigo.isEmpty()) return false;
        
        CodigoDescuento cd = optCodigo.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Validar que esté activo, dentro de las fechas válidas
        return cd.getActivo() 
            && cd.getStartDate().isBefore(now) 
            && cd.getEndDate().isAfter(now);
    }

    public Double calcularDescuento(CodigoDescuento codigo, Double precio) {
        if (codigo.getType().equals("percentage")) {
            return precio * (codigo.getValue() / 100.0);
        } else {
            return Math.min(codigo.getValue(), precio);
        }
    }
}

