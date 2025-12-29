package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.dto.request.ResenaRequest;
import com.rasengaComics.rasengaComics.dto.response.ResenaResponse;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/resenas")
public class ResenaController {

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ResenaRequest req) {
        return ResponseEntity.status(501).body(new ApiResponse(false, "Reseñas no implementadas", null));
    }
}

