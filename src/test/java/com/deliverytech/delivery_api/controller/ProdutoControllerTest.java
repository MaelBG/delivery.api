package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoDTO;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ProdutoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private PedidoRepository pedidoRepository;

    private Produto produtoSalvo;
    private Restaurante restaurante;

    @BeforeEach
    void setup() {
        // Limpeza na ordem correta para evitar erros de integridade
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();

        restaurante = new Restaurante();
        restaurante.setNome("Restaurante para Produtos");
        restaurante = restauranteRepository.save(restaurante);

        Produto produtoBase = new Produto();
        produtoBase.setNome("X-Burger Teste");
        produtoBase.setCategoria("Lanche");
        produtoBase.setDisponivel(true);
        produtoBase.setPreco(new BigDecimal("20.00"));
        produtoBase.setRestaurante(restaurante);
        produtoSalvo = produtoRepository.save(produtoBase);
    }

    @Test
    @DisplayName("Deve cadastrar produto")
    @WithMockUser(roles = "ADMIN")
    void deveCadastrarProduto() throws Exception {
        ProdutoDTO produtoDTO = new ProdutoDTO("Pizza de Teste", "Mussarela", new BigDecimal("30.00"), "Pizza", restaurante.getId(), null, true);

        mockMvc.perform(post("/api/produtos")
                .param("restauranteId", restaurante.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    @WithMockUser
    void deveBuscarProdutoPorId() throws Exception {
        mockMvc.perform(get("/api/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve atualizar produto")
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarProduto() throws Exception {
        ProdutoDTO produtoDTO = new ProdutoDTO("Nome Atualizado", "Desc", new BigDecimal("25.00"), "Lanche", restaurante.getId(), null, true);

        mockMvc.perform(put("/api/produtos/{id}", produtoSalvo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve remover produto")
    @WithMockUser(roles = "ADMIN")
    void deveRemoverProduto() throws Exception {
        mockMvc.perform(delete("/api/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve alterar disponibilidade do produto")
    @WithMockUser(roles = "ADMIN")
    void deveAlterarDisponibilidade() throws Exception {
        mockMvc.perform(patch("/api/produtos/{id}/disponibilidade", produtoSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.disponivel").value(false));
    }

    @Test
    @DisplayName("Deve buscar produtos do restaurante")
    @WithMockUser
    void deveBuscarProdutosDoRestaurante() throws Exception {
        mockMvc.perform(get("/api/produtos/restaurantes/{restauranteId}/produtos", restaurante.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    @WithMockUser
    void deveBuscarPorCategoria() throws Exception {
        mockMvc.perform(get("/api/produtos/categoria/{categoria}", "Lanche"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar produtos por nome")
    @WithMockUser
    void deveBuscarPorNome() throws Exception {
        mockMvc.perform(get("/api/produtos/buscar").param("nome", "X-Burger"))
                .andExpect(status().isOk());
    }
}