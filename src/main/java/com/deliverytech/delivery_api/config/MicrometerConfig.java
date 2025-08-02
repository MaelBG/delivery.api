package com.deliverytech.delivery_api.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config()
                .commonTags("application", "delivery-api") // [cite: 206]
                .commonTags("environment", "development") // [cite: 207]
                .meterFilter(MeterFilter.deny(id -> { // [cite: 209]
                    String uri = id.getTag("uri");
                    return uri != null && uri.startsWith("/actuator"); // [cite: 211]
                }));
        };
    }
}