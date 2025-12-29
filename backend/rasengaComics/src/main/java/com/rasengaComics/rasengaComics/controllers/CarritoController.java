package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.dto.request.CarritoRequest;
import com.rasengaComics.rasengaComics.dto.response.CarritoResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody CarritoRequest req) {
        return ResponseEntity.status(501).body(new ApiResponse(false, "Carrito no implementado", null));
    }
}

