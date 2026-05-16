package com.example.JourneyMate.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
<<<<<<< HEAD

        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:3000",
            "https://journeymate-485617.web.app",
            "https://journeymate-485617.firebaseapp.com",
            "https://journeymate-backend-ifbynfjw3a-ew.a.run.app"
        ));

=======

        // Poner todos los origenes en la misma lista
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

>>>>>>> ef7fd9b9f28060e5dbffd667b7a016200b2487cb
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
