package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.dto.request.ReservaRequest;
import com.rasengaComics.rasengaComics.dto.response.ReservaResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req) {
        return ResponseEntity.status(501).body(new ApiResponse(false, "Reservas no implementadas", null));
    }
}

