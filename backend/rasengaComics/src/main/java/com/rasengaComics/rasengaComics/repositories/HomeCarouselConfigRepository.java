package com.rasengaComics.rasengaComics.repositories;

import com.rasengaComics.rasengaComics.entities.HomeCarouselConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeCarouselConfigRepository extends JpaRepository<HomeCarouselConfig, Integer> {
}
