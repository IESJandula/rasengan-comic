package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);
    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        logger.info("【WEBHOOK RECIBIDO】 Payload length: {}", payload.length());
        
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            logger.info("【EVENT VERIFICADO】 Type: {}", event.getType());

            if ("checkout.session.completed".equals(event.getType())) {
                logger.info("【PROCESANDO CHECKOUT COMPLETADO】");
                
                try {
                    // Obtener la sesión del evento usando deserializeUnsafe
                    Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
                    logger.info("【SESSION DESERIALIZADA】 Tipo: {}", session.getClass().getSimpleName());
                    
                    if (session != null && session.getId() != null) {
                        logger.info("【SESSION VALIDA】 SessionId: {}", session.getId());
                        stripeService.procesarCheckoutCompletado(session);
                        logger.info("【PEDIDO CREADO EXITOSAMENTE】");
                    } else {
                        logger.error("【ERROR】 Session o ID es null");
                    }
                } catch (Exception e) {
                    logger.error("【ERROR】 Error procesando sesión: {}", e.getMessage(), e);
                }
            } else {
                logger.info("【EVENTO IGNORADO】 Type: {}", event.getType());
            }

            return ResponseEntity.ok("");
        } catch (SignatureVerificationException e) {
            logger.error("【ERROR】 Firma de Stripe inválida", e);
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            logger.error("【ERROR】 Exception inesperada", e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
