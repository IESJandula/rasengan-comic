package com.rasengaComics.rasengaComics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        FirebaseTokenFilter firebaseFilter = new FirebaseTokenFilter();

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/products/**").permitAll() // Permitir acceso público a la API de productos
                    .requestMatchers("/api/discounts/**").permitAll() // Permitir acceso a la API de descuentos
                    .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/productos/**").permitAll() // TEMP: permitir POST para pruebas
                    .requestMatchers("/eventos/**").permitAll() // Permitir acceso completo a eventos (GET, POST, PUT, DELETE)
                    .requestMatchers(HttpMethod.GET, "/reservas/**").permitAll() // Permitir lectura de reservas
                    .requestMatchers(HttpMethod.POST, "/stripe/webhook").permitAll()
                    .requestMatchers("/hola", "/public/**", "/auth/**", "/error").permitAll()
                    .anyRequest().authenticated()
                )
                .addFilterBefore(firebaseFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(allowedOrigins, "http://localhost:5174"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}