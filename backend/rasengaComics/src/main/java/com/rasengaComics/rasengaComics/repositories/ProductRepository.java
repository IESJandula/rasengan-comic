package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Buscar por categoría
    List<Product> findByCategory(String category);
    
    // Buscar por subcategoría
    List<Product> findBySubcategory(String subcategory);
    
    // Buscar productos disponibles
    List<Product> findByAvailableTrue();
    
    // Buscar productos de reserva
    List<Product> findByIsReserveTrue();
    
    // Buscar productos nuevos
    List<Product> findByIsNewTrue();
    
    // Buscar por categoría y disponibilidad
    List<Product> findByCategoryAndAvailableTrue(String category);
}
