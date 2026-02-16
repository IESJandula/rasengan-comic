package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.dto.request.PedidoRequest;
import com.rasengaComics.rasengaComics.dto.request.StripeCheckoutRequest;
import com.rasengaComics.rasengaComics.dto.response.StripeCheckoutResponse;
import com.rasengaComics.rasengaComics.models.Producto;
import com.rasengaComics.rasengaComics.models.Usuario;
import com.rasengaComics.rasengaComics.repositories.ProductoRepository;
import com.rasengaComics.rasengaComics.repositories.UsuarioRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListLineItemsParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StripeService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoService pedidoService;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public StripeService(ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository,
                         PedidoService pedidoService) {
        this.productoRepository = productoRepository;
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
        for (StripeCheckoutRequest.Item item : request.getItems()) {
            if (item.getProductoId() == null || item.getCantidad() == null) {
                throw new IllegalArgumentException("items incompletos");
            }
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));

            long unitAmount = Math.round(producto.getPrecio() * 100);

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(producto.getNombre())
                            .setDescription(producto.getDescripcion())
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
                .putMetadata("usuarioUid", request.getUsuarioUid())
                .addAllLineItem(lineItems)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .build();

        Session session = Session.create(params);
        return new StripeCheckoutResponse(session.getId(), session.getUrl());
    }

    public void procesarCheckoutCompletado(Session session) throws StripeException {
        if (session == null) {
            return;
        }

        String usuarioUid = session.getMetadata().get("usuarioUid");
        if (usuarioUid == null || usuarioUid.isBlank()) {
            return;
        }

        SessionListLineItemsParams listParams = SessionListLineItemsParams.builder()
                .addExpand("data.price.product")
                .build();
        Session fullSession = Session.retrieve(session.getId());
        LineItemCollection lineItems = fullSession.listLineItems(listParams);

        List<PedidoRequest.Item> items = new ArrayList<>();
        for (LineItem lineItem : lineItems.getData()) {
            Price price = lineItem.getPrice();
            if (price == null) {
                continue;
            }
            Product product = price.getProductObject();
            if (product == null || product.getMetadata() == null) {
                continue;
            }
            String productoId = product.getMetadata().get("productoId");
            if (productoId == null) {
                continue;
            }

            PedidoRequest.Item pedidoItem = new PedidoRequest.Item();
            pedidoItem.setProductoId(Long.parseLong(productoId));
            pedidoItem.setCantidad(lineItem.getQuantity() != null ? lineItem.getQuantity().intValue() : 1);
            items.add(pedidoItem);
        }

        if (!items.isEmpty()) {
            pedidoService.crearPedidoPagado(
                    usuarioUid,
                    items,
                    session.getId(),
                    session.getPaymentIntent()
            );
        }
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
