package com.example.JourneyMate.config.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "booking.api")
@Data
public class BookingProperties {
    private String key;
    private String host;
}
