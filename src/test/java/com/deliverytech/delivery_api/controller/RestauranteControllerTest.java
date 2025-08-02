package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.RestauranteDTO;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RestauranteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ProdutoRepository produtoRepository;

    private Restaurante restauranteSalvo;

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();

        Restaurante restauranteBase = new Restaurante();
        restauranteBase.setNome("Burger King");
        restauranteBase.setCategoria("JAPONESA");
        restauranteBase.setAtivo(true);
        restauranteBase.setTaxaEntrega(new BigDecimal("5.00"));
        restauranteSalvo = restauranteRepository.save(restauranteBase);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarRestaurante() throws Exception {
        RestauranteDTO restauranteDTO = new RestauranteDTO("Pizza Hut", "ITALIANA", "Rua X", "11999999999", new BigDecimal("4.00"), 30, "10:00-22:00", "contato@pizzahut.com");
        mockMvc.perform(post("/api/restaurantes").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void deveListarRestaurantes() throws Exception {
        mockMvc.perform(get("/api/restaurantes")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveBuscarRestaurantePorId() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", restauranteSalvo.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarRestaurante() throws Exception {
        RestauranteDTO restauranteDTO = new RestauranteDTO("Nome Atualizado", "ITALIANA", "Rua X", "11999999999", new BigDecimal("4.00"), 30, "10:00-22:00", "contato@pizzahut.com");
        mockMvc.perform(put("/api/restaurantes/{id}", restauranteSalvo.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAlterarStatusRestaurante() throws Exception {
        mockMvc.perform(patch("/api/restaurantes/{id}/status", restauranteSalvo.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveBuscarPorCategoria() throws Exception {
        mockMvc.perform(get("/api/restaurantes/categoria/{categoria}", "JAPONESA")).andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser
    void deveCalcularTaxaEntrega() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}/taxa-entrega/{cep}", restauranteSalvo.getId(), "12345-678")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveBuscarProximos() throws Exception {
        mockMvc.perform(get("/api/restaurantes/proximos/{cep}", "12345-678")).andExpect(status().isOk());
    }
}