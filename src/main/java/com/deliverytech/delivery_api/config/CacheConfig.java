package com.deliverytech.delivery_api.config; // Verifique se o nome do pacote está correto

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Aqui estamos criando um gerenciador de cache simples que reconhece
        // o cache chamado "clientes".
        return new ConcurrentMapCacheManager("clientes");
    }
}