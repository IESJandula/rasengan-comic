package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.request.StripeCheckoutRequest;
import com.rasengaComics.rasengaComics.dto.response.StripeCheckoutResponse;
import com.rasengaComics.rasengaComics.entities.Product;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListLineItemsParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);
    private final ProductRepository productRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoService pedidoService;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public StripeService(ProductRepository productRepository,
                         UsuarioRepository usuarioRepository,
                         PedidoService pedidoService) {
        this.productRepository = productRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoService = pedidoService;
    }

    public StripeCheckoutResponse crearCheckoutSession(StripeCheckoutRequest request) throws StripeException {
        if (request.getUsuarioUid() == null || request.getUsuarioUid().isBlank()) {
            throw new IllegalArgumentException("usuarioUid es requerido");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("items es requerido");
        }

        asegurarUsuario(request);

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        String itemsMetadata = request.getItems().stream()
            .map(i -> i.getProductoId() + ":" + i.getCantidad())
            .collect(Collectors.joining(","));

        for (StripeCheckoutRequest.Item item : request.getItems()) {
            if (item.getProductoId() == null || item.getCantidad() == null) {
                throw new IllegalArgumentException("items incompletos");
            }
            Product producto = productRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));

            long unitAmount = Math.round(producto.getPrice() * 100);

            // Construir descripción con categoría y subcategoría
            String descripcion = producto.getCategory();
            if (producto.getSubcategory() != null && !producto.getSubcategory().isEmpty()) {
                descripcion += " - " + producto.getSubcategory();
            }

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(producto.getName())
                            .setDescription(descripcion)
                            .putMetadata("productoId", producto.getId().toString())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount(unitAmount)
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(item.getCantidad().longValue())
                    .setPriceData(priceData)
                    .build();

            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
            .setClientReferenceId(request.getUsuarioUid())
                .putMetadata("usuarioUid", request.getUsuarioUid())
            .putMetadata("items", itemsMetadata)
                .addAllLineItem(lineItems)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .build();

        Session session = Session.create(params);
        return new StripeCheckoutResponse(session.getId(), session.getUrl());
    }

    public void procesarCheckoutCompletado(Session session) throws StripeException {
        logger.info("【PROCESANDO CHECKOUT】 SessionId: {}", session.getId());
        
        if (session == null) {
            logger.error("【ERROR】 Session es null");
            return;
        }

        String usuarioUid = null;
        if (session.getMetadata() != null) {
            usuarioUid = session.getMetadata().get("usuarioUid");
        }
        if (usuarioUid == null || usuarioUid.isBlank()) {
            usuarioUid = session.getClientReferenceId();
        }
        logger.info("【USUARIO UID】 {}", usuarioUid);
        
        if (usuarioUid == null || usuarioUid.isBlank()) {
            logger.error("【ERROR】 usuarioUid no encontrado en metadata");
            return;
        }

        SessionListLineItemsParams listParams = SessionListLineItemsParams.builder()
                .addExpand("data.price.product")
                .build();
        Session fullSession = Session.retrieve(session.getId());
        LineItemCollection lineItems = fullSession.listLineItems(listParams);
        
        logger.info("【LINE ITEMS】 Total items en session: {}", lineItems.getData().size());

        List<PedidoRequest.Item> items = new ArrayList<>();
        for (LineItem lineItem : lineItems.getData()) {
            logger.info("【Item procesado】 Quantity: {}", lineItem.getQuantity());
            
            Price price = lineItem.getPrice();
            if (price == null) {
                logger.warn("【SKIP】 Price es null");
                continue;
            }
            
            com.stripe.model.Product product = price.getProductObject();
            if (product == null || product.getMetadata() == null) {
                logger.warn("【SKIP】 Product es null o sin metadata");
                continue;
            }
            
            String productoId = product.getMetadata().get("productoId");
            logger.info("【PRODUCTO ID】 {}", productoId);
            
            if (productoId == null) {
                logger.warn("【SKIP】 productoId no encontrado en metadata");
                continue;
            }

            PedidoRequest.Item pedidoItem = new PedidoRequest.Item();
            pedidoItem.setProductoId(Long.parseLong(productoId));
            pedidoItem.setCantidad(lineItem.getQuantity() != null ? lineItem.getQuantity().intValue() : 1);
            items.add(pedidoItem);
            
            logger.info("【ITEM AGREGADO】 ProductoId: {}, Cantidad: {}", productoId, pedidoItem.getCantidad());
        }

        // Fallback: si Stripe no devuelve metadata de line items, usar metadata de la session
        if (items.isEmpty() && session.getMetadata() != null) {
            String serializedItems = session.getMetadata().get("items");
            if (serializedItems != null && !serializedItems.isBlank()) {
                logger.info("【FALLBACK ITEMS】 Reconstruyendo items desde metadata de session");
                String[] pairs = serializedItems.split(",");
                for (String pair : pairs) {
                    String[] values = pair.split(":");
                    if (values.length != 2) {
                        continue;
                    }
                    try {
                        Long productoId = Long.parseLong(values[0]);
                        Integer cantidad = Integer.parseInt(values[1]);
                        PedidoRequest.Item pedidoItem = new PedidoRequest.Item();
                        pedidoItem.setProductoId(productoId);
                        pedidoItem.setCantidad(cantidad);
                        items.add(pedidoItem);
                    } catch (NumberFormatException ex) {
                        logger.warn("【FALLBACK ITEMS】 Formato inválido en metadata item: {}", pair);
                    }
                }
            }
        }

        logger.info("【TOTAL ITEMS PROCESADOS】 {}", items.size());
        
        if (!items.isEmpty()) {
            logger.info("【CREANDO PEDIDO】 usuarioUid: {}, items: {}", usuarioUid, items.size());
            try {
                pedidoService.crearPedidoPagado(
                        usuarioUid,
                        items,
                        session.getId(),
                        session.getPaymentIntent()
                );
                logger.info("【PEDIDO CREADO EXITOSAMENTE】");
            } catch (Exception e) {
                logger.error("【ERROR AL CREAR PEDIDO】", e);
                throw e;
            }
        } else {
            logger.warn("【ADVERTENCIA】 No hay items para procesar");
        }
    }

    public void confirmarSesionCheckout(String sessionId) throws StripeException {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId es requerido");
        }

        Session session = Session.retrieve(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Sesión de Stripe no encontrada");
        }

        String paymentStatus = session.getPaymentStatus();
        if (!"paid".equalsIgnoreCase(paymentStatus)) {
            throw new IllegalArgumentException("La sesión aún no está pagada");
        }

        procesarCheckoutCompletado(session);
    }

    private void asegurarUsuario(StripeCheckoutRequest request) {
        Optional<Usuario> existente = usuarioRepository.findById(request.getUsuarioUid());
        if (existente.isPresent()) {
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setUid(request.getUsuarioUid());
        nuevo.setEmail(request.getUsuarioEmail());
        nuevo.setNombre(request.getUsuarioNombre());
        nuevo.setRol("USER");
        usuarioRepository.save(nuevo);
    }
}
