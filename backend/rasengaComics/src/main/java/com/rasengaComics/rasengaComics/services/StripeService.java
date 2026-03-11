package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.request.StripeCheckoutRequest;
import com.rasengaComics.rasengaComics.dto.response.StripeCheckoutResponse;
import com.rasengaComics.rasengaComics.entities.Product;
import com.rasengaComics.rasengaComics.models.CodigoDescuento;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Coupon;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.CouponCreateParams;
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
    private static final double ENVIO_GRATIS_UMBRAL = 50.0;
    private static final double COSTE_ENVIO = 10.0;

    private final ProductRepository productRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoService pedidoService;
    private final CodigoDescuentoService codigoDescuentoService;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public StripeService(ProductRepository productRepository,
                         UsuarioRepository usuarioRepository,
                         PedidoService pedidoService,
                         CodigoDescuentoService codigoDescuentoService) {
        this.productRepository = productRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoService = pedidoService;
        this.codigoDescuentoService = codigoDescuentoService;
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
        String metodoEntrega = normalizarMetodoEntrega(request.getMetodoEntrega());
        String codigoDescuento = normalizarCodigoDescuento(request.getCodigoDescuento());
        logger.info("【CHECKOUT REQUEST】 uid={}, metodoEntrega={}, codigoDescuento={}",
            request.getUsuarioUid(), metodoEntrega, codigoDescuento);
        String itemsMetadata = request.getItems().stream()
            .map(i -> i.getProductoId() + ":" + i.getCantidad())
            .collect(Collectors.joining(","));
        long subtotalCents = 0L;

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
            subtotalCents += (unitAmount * item.getCantidad().longValue());
        }

        long shippingCents = calcularEnvioCents(metodoEntrega, subtotalCents);
        logger.info("【CHECKOUT IMPORTES】 subtotalCents={}, shippingCents={}", subtotalCents, shippingCents);
        if (shippingCents > 0) {
            SessionCreateParams.LineItem.PriceData.ProductData envioProductData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName("Envío a domicilio")
                            .setDescription("Coste de envío")
                            .putMetadata("lineItemType", "shipping")
                            .build();

            SessionCreateParams.LineItem.PriceData envioPriceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount(shippingCents)
                            .setProductData(envioProductData)
                            .build();

            SessionCreateParams.LineItem envioLineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(envioPriceData)
                    .build();

            lineItems.add(envioLineItem);
        }

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
            .setClientReferenceId(request.getUsuarioUid())
                .putMetadata("usuarioUid", request.getUsuarioUid())
            .putMetadata("metodoEntrega", metodoEntrega)
            .putMetadata("items", itemsMetadata)
                .addAllLineItem(lineItems)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);

        if (shippingCents > 0) {
            paramsBuilder.putMetadata("shippingCents", String.valueOf(shippingCents));
        }

        if (codigoDescuento != null) {
            long discountCents = calcularDescuentoCents(codigoDescuento, subtotalCents, shippingCents);
            logger.info("【CHECKOUT DESCUENTO】 codigo={}, discountCents={}", codigoDescuento, discountCents);
            if (discountCents > 0) {
                CouponCreateParams couponParams = CouponCreateParams.builder()
                        .setDuration(CouponCreateParams.Duration.ONCE)
                        .setCurrency("eur")
                        .setAmountOff(discountCents)
                        .setName("Código " + codigoDescuento)
                        .build();

                Coupon coupon = Coupon.create(couponParams);
                paramsBuilder.addDiscount(
                        SessionCreateParams.Discount.builder()
                                .setCoupon(coupon.getId())
                                .build()
                );
                paramsBuilder.putMetadata("codigoDescuento", codigoDescuento);
            }
        }

        SessionCreateParams params = paramsBuilder.build();

        Session session = Session.create(params);
        logger.info("【CHECKOUT SESSION CREADA】 sessionId={}, lineItems={}", session.getId(), lineItems.size());
        return new StripeCheckoutResponse(session.getId(), session.getUrl());
    }

    public void procesarCheckoutCompletado(Session session) throws StripeException {
        if (session == null) {
            logger.error("【ERROR】 Session es null");
            return;
        }

        logger.info("【PROCESANDO CHECKOUT】 SessionId: {}", session.getId());

        String usuarioUid = null;
        String metodoEntrega = "envio";
        if (session.getMetadata() != null) {
            usuarioUid = session.getMetadata().get("usuarioUid");
            metodoEntrega = normalizarMetodoEntrega(session.getMetadata().get("metodoEntrega"));
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
                    metodoEntrega,
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

    private String normalizarMetodoEntrega(String metodoEntrega) {
        if (metodoEntrega == null) {
            return "envio";
        }
        String normalizado = metodoEntrega.trim().toLowerCase();
        if ("tienda".equals(normalizado)) {
            return "tienda";
        }
        return "envio";
    }

    private String normalizarCodigoDescuento(String codigoDescuento) {
        if (codigoDescuento == null) {
            return null;
        }
        String normalizado = codigoDescuento.trim().toUpperCase();
        return normalizado.isEmpty() ? null : normalizado;
    }

    private long calcularEnvioCents(String metodoEntrega, long subtotalCents) {
        if ("tienda".equals(metodoEntrega)) {
            return 0L;
        }
        double subtotalEuros = subtotalCents / 100.0;
        if (subtotalEuros > ENVIO_GRATIS_UMBRAL) {
            return 0L;
        }
        return Math.round(COSTE_ENVIO * 100);
    }

    private long calcularDescuentoCents(String codigoDescuento, long subtotalCents, long shippingCents) {
        Optional<CodigoDescuento> optCodigo = codigoDescuentoService.obtenerPorCodigo(codigoDescuento);
        if (optCodigo.isEmpty() || !codigoDescuentoService.validarCodigo(codigoDescuento)) {
            throw new IllegalArgumentException("Código promocional inválido o expirado");
        }

        CodigoDescuento codigo = optCodigo.get();
        double subtotalEuros = subtotalCents / 100.0;
        double descuentoEuros = codigoDescuentoService.calcularDescuento(codigo, subtotalEuros);

        long descuentoCents = Math.round(descuentoEuros * 100);
        long maxDescuentoCents = subtotalCents + shippingCents;
        return Math.max(0L, Math.min(descuentoCents, maxDescuentoCents));
    }
}
