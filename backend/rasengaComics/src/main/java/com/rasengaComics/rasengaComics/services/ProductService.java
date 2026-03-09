package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.entities.Product;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.ProductCollection;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductListParams;
import com.stripe.param.ProductUpdateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    // Obtener todos los productos
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Obtener producto por ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Crear nuevo producto
    @Transactional
    public Product createProduct(Product product) {
        Product createdProduct = productRepository.save(product);

        try {
            // Sincronizar el catalogo con Stripe al crear un producto en el panel admin.
            crearProductoEnStripe(createdProduct);
            return createdProduct;
        } catch (StripeException e) {
            // Compensacion: evitar que quede creado en BD si Stripe falla.
            productRepository.deleteById(createdProduct.getId());
            logger.error("Error al crear producto en Stripe. Se revierte producto local id={}", createdProduct.getId(), e);
            throw new RuntimeException("No se pudo crear el producto en Stripe", e);
        }
    }

    private void crearProductoEnStripe(Product product) throws StripeException {
        StringBuilder descriptionBuilder = new StringBuilder();
        if (product.getCategory() != null) {
            descriptionBuilder.append(product.getCategory());
        }
        if (product.getSubcategory() != null && !product.getSubcategory().isBlank()) {
            if (descriptionBuilder.length() > 0) {
                descriptionBuilder.append(" - ");
            }
            descriptionBuilder.append(product.getSubcategory());
        }

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(product.getName())
                .setDescription(descriptionBuilder.toString())
                .putMetadata("origen", "admin")
                .putMetadata("localProductId", String.valueOf(product.getId()))
                .build();

        com.stripe.model.Product stripeProduct = com.stripe.model.Product.create(params);
        logger.info("Producto creado en Stripe: localId={}, stripeId={}", product.getId(), stripeProduct.getId());
    }
    
    // Actualizar producto
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
        
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setSubcategory(productDetails.getSubcategory());
        product.setPrice(productDetails.getPrice());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setDiscount(productDetails.getDiscount());
        product.setImage(productDetails.getImage());
        product.setAvailable(productDetails.getAvailable());
        product.setStock(productDetails.getStock());
        product.setRating(productDetails.getRating());
        product.setReviews(productDetails.getReviews());
        product.setIsReserve(productDetails.getIsReserve());
        product.setIsNew(productDetails.getIsNew());
        
        Product updatedProduct = productRepository.save(product);

        try {
            actualizarProductoEnStripe(updatedProduct);
            return updatedProduct;
        } catch (StripeException e) {
            logger.error("Error al actualizar producto en Stripe. Se revierte actualizacion local id={}", updatedProduct.getId(), e);
            throw new RuntimeException("No se pudo actualizar el producto en Stripe", e);
        }
    }
    
    // Eliminar producto
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));

        try {
            eliminarProductoEnStripe(product.getId());
        } catch (StripeException e) {
            logger.error("Error al eliminar producto en Stripe. Se cancela eliminacion local id={}", product.getId(), e);
            throw new RuntimeException("No se pudo eliminar el producto en Stripe", e);
        }

        productRepository.deleteById(id);
    }

    private void actualizarProductoEnStripe(Product product) throws StripeException {
        com.stripe.model.Product stripeProduct = buscarProductoStripePorLocalId(product.getId());
        if (stripeProduct == null) {
            logger.warn("No se encontro producto en Stripe para localId={}. Se crea uno nuevo.", product.getId());
            crearProductoEnStripe(product);
            return;
        }

        StringBuilder descriptionBuilder = new StringBuilder();
        if (product.getCategory() != null) {
            descriptionBuilder.append(product.getCategory());
        }
        if (product.getSubcategory() != null && !product.getSubcategory().isBlank()) {
            if (descriptionBuilder.length() > 0) {
                descriptionBuilder.append(" - ");
            }
            descriptionBuilder.append(product.getSubcategory());
        }

        ProductUpdateParams updateParams = ProductUpdateParams.builder()
                .setName(product.getName())
                .setDescription(descriptionBuilder.toString())
                .putMetadata("origen", "admin")
                .putMetadata("localProductId", String.valueOf(product.getId()))
                .build();

        stripeProduct.update(updateParams);
        logger.info("Producto actualizado en Stripe: localId={}, stripeId={}", product.getId(), stripeProduct.getId());
    }

    private void eliminarProductoEnStripe(Long localProductId) throws StripeException {
        com.stripe.model.Product stripeProduct = buscarProductoStripePorLocalId(localProductId);
        if (stripeProduct == null) {
            logger.warn("No se encontro producto en Stripe para eliminar localId={}", localProductId);
            return;
        }

        stripeProduct.delete();
        logger.info("Producto eliminado en Stripe: localId={}, stripeId={}", localProductId, stripeProduct.getId());
    }

    private com.stripe.model.Product buscarProductoStripePorLocalId(Long localProductId) throws StripeException {
        ProductListParams params = ProductListParams.builder()
                .setLimit(100L)
                .setActive(true)
                .build();

        ProductCollection collection = com.stripe.model.Product.list(params);
        for (com.stripe.model.Product stripeProduct : collection.autoPagingIterable()) {
            String metadataLocalId = stripeProduct.getMetadata() != null
                    ? stripeProduct.getMetadata().get("localProductId")
                    : null;
            if (String.valueOf(localProductId).equals(metadataLocalId)) {
                return stripeProduct;
            }
        }
        return null;
    }
    
    // Buscar por categoría
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // Buscar productos disponibles
    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }
    
    // Buscar productos de reserva
    public List<Product> getReserveProducts() {
        return productRepository.findByIsReserveTrue();
    }
    
    // Buscar productos nuevos
    public List<Product> getNewProducts() {
        return productRepository.findByIsNewTrue();
    }
}
