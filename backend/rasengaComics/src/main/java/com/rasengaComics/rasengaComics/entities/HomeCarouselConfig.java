package com.rasengaComics.rasengaComics.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "home_carousel_config")
public class HomeCarouselConfig {

    @Id
    private Integer id;

    @Column(length = 1000)
    private String slide1;

    @Column(length = 1000)
    private String slide2;

    @Column(length = 1000)
    private String slide3;

    public HomeCarouselConfig() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlide1() {
        return slide1;
    }

    public void setSlide1(String slide1) {
        this.slide1 = slide1;
    }

    public String getSlide2() {
        return slide2;
    }

    public void setSlide2(String slide2) {
        this.slide2 = slide2;
    }

    public String getSlide3() {
        return slide3;
    }

    public void setSlide3(String slide3) {
        this.slide3 = slide3;
    }
}
