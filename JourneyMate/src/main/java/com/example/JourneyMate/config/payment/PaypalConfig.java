package com.example.JourneyMate.config.payment;

import com.paypal.base.rest.APIContext;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

    @Autowired
    private Dotenv dotenv;

    @Bean
    public APIContext apiContext() {

        String clientId = dotenv.get("PAYPAL_CLIENT_ID");
        String clientSecret = dotenv.get("PAYPAL_SECRET");

        // Fijamos el modo manualmente (sandbox por defecto)
        String mode = "sandbox";

        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);

        APIContext context = new APIContext(clientId, clientSecret, mode);
        context.setConfigurationMap(configMap);

        return context;
    }
}
