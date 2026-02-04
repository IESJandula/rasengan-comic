package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.ProductService;
import com.rasengaComics.rasengaComics.services.UsuarioService;
import com.rasengaComics.rasengaComics.services.EventoService;
import com.rasengaComics.rasengaComics.services.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final ProductService productService;
    private final UsuarioService usuarioService;
    private final EventoService eventoService;
    private final ReservaService reservaService;

    public EstadisticasController(ProductService productService, UsuarioService usuarioService, 
                                   EventoService eventoService, ReservaService reservaService) {
        this.productService = productService;
        this.usuarioService = usuarioService;
        this.eventoService = eventoService;
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Obtener cantidad de productos
        long totalProductos = productService.getAllProducts().size();
        
        // Obtener cantidad de usuarios
        long totalUsuarios = usuarioService.countAll();
        
        // Obtener cantidad de eventos
        long totalEventos = eventoService.countAll();
        
        // Obtener cantidad de reservas activas
        long reservasActivas = reservaService.countActivas();
        
        estadisticas.put("totalProductos", totalProductos);
        estadisticas.put("totalUsuarios", totalUsuarios);
        estadisticas.put("totalEventos", totalEventos);
        estadisticas.put("reservasActivas", reservasActivas);
        
        return ResponseEntity.ok(estadisticas);
    }
}
