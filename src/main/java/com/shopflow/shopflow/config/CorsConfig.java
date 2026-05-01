package com.shopflow.shopflow.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Origines autorisées (frontend)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://shopflow-3a2d7.web.app",
                "https://shopflow-3a2d7.firebaseapp.com",
                "https://*.web.app",
                "https://*.firebaseapp.com"
        ));

        // Méthodes HTTP autorisées
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers autorisés
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        // Autoriser l'envoi de cookies/credentials
        config.setAllowCredentials(true);

        // Durée du cache preflight (en secondes)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}