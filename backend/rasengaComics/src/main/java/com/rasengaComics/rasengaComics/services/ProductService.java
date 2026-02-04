package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.entities.Product;
import com.rasengaComics.rasengaComics.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
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
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    // Actualizar producto
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setSubcategory(productDetails.getSubcategory());
        product.setPrice(productDetails.getPrice());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setDiscount(productDetails.getDiscount());
        product.setImage(productDetails.getImage());
        product.setAvailable(productDetails.getAvailable());
        product.setRating(productDetails.getRating());
        product.setReviews(productDetails.getReviews());
        product.setIsReserve(productDetails.getIsReserve());
        product.setIsNew(productDetails.getIsNew());
        
        return productRepository.save(product);
    }
    
    // Eliminar producto
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
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
