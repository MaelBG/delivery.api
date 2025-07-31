package com.deliverytech.delivery_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // Importe esta classe

@SpringBootTest
@ActiveProfiles("test") // Garante que as configurações de teste serão usadas
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}