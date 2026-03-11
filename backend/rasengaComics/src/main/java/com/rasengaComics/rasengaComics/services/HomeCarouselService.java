package com.rasengaComics.rasengaComics.services;

import com.rasengaComics.rasengaComics.entities.HomeCarouselConfig;
import com.rasengaComics.rasengaComics.repositories.HomeCarouselConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeCarouselService {

    private static final int CONFIG_ID = 1;

    private final HomeCarouselConfigRepository repository;

    public HomeCarouselService(HomeCarouselConfigRepository repository) {
        this.repository = repository;
    }

    public List<String> getSlides() {
        HomeCarouselConfig config = getOrCreateConfig();
        List<String> slides = new ArrayList<>();
        slides.add(sanitize(config.getSlide1()));
        slides.add(sanitize(config.getSlide2()));
        slides.add(sanitize(config.getSlide3()));
        return slides;
    }

    public List<String> updateSlides(List<String> incomingSlides) {
        HomeCarouselConfig config = getOrCreateConfig();

        String slide1 = getSlideAt(incomingSlides, 0);
        String slide2 = getSlideAt(incomingSlides, 1);
        String slide3 = getSlideAt(incomingSlides, 2);

        config.setSlide1(slide1);
        config.setSlide2(slide2);
        config.setSlide3(slide3);

        repository.save(config);
        return getSlides();
    }

    private HomeCarouselConfig getOrCreateConfig() {
        return repository.findById(CONFIG_ID).orElseGet(() -> {
            HomeCarouselConfig config = new HomeCarouselConfig();
            config.setId(CONFIG_ID);
            config.setSlide1("");
            config.setSlide2("");
            config.setSlide3("");
            return repository.save(config);
        });
    }

    private String getSlideAt(List<String> slides, int index) {
        if (slides == null || index >= slides.size()) {
            return "";
        }
        return sanitize(slides.get(index));
    }

    private String sanitize(String value) {
        return value == null ? "" : value.trim();
    }
}
