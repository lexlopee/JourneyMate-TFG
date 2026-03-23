package com.example.JourneyMate.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv(StandardEnvironment env) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        Map<String, Object> envMap = new HashMap<>();
        for (DotenvEntry entry : dotenv.entries()) {
            envMap.put(entry.getKey(), entry.getValue());
        }

        env.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envMap));

        return dotenv;
    }
}
