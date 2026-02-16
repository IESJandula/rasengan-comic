package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.dto.request.StripeCheckoutRequest;
import com.rasengaComics.rasengaComics.dto.response.ApiResponse;
import com.rasengaComics.rasengaComics.services.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout-session")
    public ResponseEntity<?> crearCheckoutSession(@RequestBody StripeCheckoutRequest request) {
        try {
            return ResponseEntity.ok(stripeService.crearCheckoutSession(request));
        } catch (IllegalArgumentException | StripeException e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
