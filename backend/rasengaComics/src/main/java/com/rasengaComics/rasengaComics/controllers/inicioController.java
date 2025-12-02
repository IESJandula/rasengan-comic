package com.rasengaComics.rasengaComics.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class inicioController {
    @GetMapping("")
    public String hola(){
        return "hola";
    }
}
