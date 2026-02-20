package com.example.JourneyMate.config.payment;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}") // Buscamos esta propiedad en application.properties
    private String secretKey;

    @PostConstruct
    public void init() {
        // Esto configura Stripe de forma global para toda la app al arrancar
        Stripe.apiKey = secretKey;
    }
}