package com.rasengaComics.rasengaComics.controllers;

import com.rasengaComics.rasengaComics.services.HomeCarouselService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home-carousel")
public class HomeCarouselController {

    private final HomeCarouselService homeCarouselService;

    public HomeCarouselController(HomeCarouselService homeCarouselService) {
        this.homeCarouselService = homeCarouselService;
    }

    @GetMapping
    public ResponseEntity<HomeCarouselResponse> getHomeCarousel() {
        List<String> slides = homeCarouselService.getSlides();
        return ResponseEntity.ok(new HomeCarouselResponse(slides));
    }

    @PutMapping
    public ResponseEntity<HomeCarouselResponse> updateHomeCarousel(@RequestBody HomeCarouselUpdateRequest request) {
        List<String> slides = homeCarouselService.updateSlides(request != null ? request.getSlides() : null);
        return ResponseEntity.ok(new HomeCarouselResponse(slides));
    }

    public static class HomeCarouselUpdateRequest {
        private List<String> slides;

        public List<String> getSlides() {
            return slides;
        }

        public void setSlides(List<String> slides) {
            this.slides = slides;
        }
    }

    public static class HomeCarouselResponse {
        private List<String> slides;

        public HomeCarouselResponse(List<String> slides) {
            this.slides = slides;
        }

        public List<String> getSlides() {
            return slides;
        }

        public void setSlides(List<String> slides) {
            this.slides = slides;
        }
    }
}
