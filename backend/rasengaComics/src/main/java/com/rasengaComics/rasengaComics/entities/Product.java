package com.rasengaComics.rasengaComics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String category;
    
    private String subcategory;
    
    @Column(nullable = false)
    private Double price;
    
    private Double originalPrice;
    
    private Integer discount;
    
    @Column(nullable = false)
    private String image;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(nullable = false)
    private Double rating = 0.0;
    
    @Column(nullable = false)
    private Integer reviews = 0;
    
    @Column(nullable = false)
    private Boolean isReserve = false;
    
    @Column(nullable = false)
    private Boolean isNew = false;
    
    // Constructors
    public Product() {}
    
    public Product(String name, String category, String subcategory, Double price, 
                   Double originalPrice, Integer discount, String image, Boolean available, 
                   Double rating, Integer reviews, Boolean isReserve, Boolean isNew) {
        this.name = name;
        this.category = category;
        this.subcategory = subcategory;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.image = image;
        this.available = available;
        this.rating = rating;
        this.reviews = reviews;
        this.isReserve = isReserve;
        this.isNew = isNew;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Double getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public Integer getDiscount() {
        return discount;
    }
    
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getReviews() {
        return reviews;
    }
    
    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }
    
    public Boolean getIsReserve() {
        return isReserve;
    }
    
    public void setIsReserve(Boolean isReserve) {
        this.isReserve = isReserve;
    }
    
    public Boolean getIsNew() {
        return isNew;
    }
    
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }
}
