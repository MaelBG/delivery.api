package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoDTO;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository; // Adicionado para deleteAll em setup
import com.deliverytech.delivery_api.repository.ClienteRepository; // Adicionado para deleteAll em setup
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List; // Se usado para listas (e.g., hasSize)

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProdutoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private RestauranteRepository restauranteRepository; // Necessário para criar restaurantes de teste
    @Autowired
    private PedidoRepository pedidoRepository; // <-- ADICIONADO
    @Autowired
    private ClienteRepository clienteRepository; // <-- ADICIONADO

    private ProdutoDTO produtoDTO;
    private Produto produtoSalvo;
    private Restaurante restauranteTeste; // Restaurante para associar produtos

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();    // Pedido depende de Produto, Cliente e Restaurante
        produtoRepository.deleteAll();   // Produto depende de Restaurante
        restauranteRepository.deleteAll(); // Restaurante pode ser excluído agora
        clienteRepository.deleteAll();   // Cliente pode ser excluído agora


        // Criar um restaurante de teste para associar os produtos
        Restaurante restaurante = new Restaurante();
        restaurante.setNome("Sabor Teste");
        restaurante.setCategoria("Teste");
        restaurante.setEndereco("Rua Teste, 123");
        restaurante.setTelefone("11999990000");
        restaurante.setTaxaEntrega(new BigDecimal("2.00"));
        restaurante.setAvaliacao(new BigDecimal("4.0"));
        restaurante.setAtivo(true);
        restauranteTeste = restauranteRepository.save(restaurante);

        // DTO para testes de criação/atualização
        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Pizza Margherita Teste");
        produtoDTO.setDescricao("Molho, queijo, manjericão");
        produtoDTO.setPreco(new BigDecimal("30.00"));
        produtoDTO.setCategoria("Pizza");

        // Produto já existente para testes de busca/atualização/remoção
        Produto produtoBase = new Produto();
        produtoBase.setNome("X-Burger Teste");
        produtoBase.setDescricao("Hambúrguer com queijo");
        produtoBase.setPreco(new BigDecimal("20.00"));
        produtoBase.setCategoria("Lanche");
        produtoBase.setDisponivel(true);
        produtoBase.setRestaurante(restauranteTeste); // Associar ao restaurante de teste
        produtoSalvo = produtoRepository.save(produtoBase);
    }

    @Test
    void deveCadastrarProdutoComSucesso() throws Exception {
        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(produtoDTO))
                .param("restauranteId", restauranteTeste.getId().toString())) // Passa o ID do restaurante
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Pizza Margherita Teste"))
                .andExpect(jsonPath("$.data.categoria").value("Pizza"))
                .andExpect(jsonPath("$.data.disponivel").value(true));
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        mockMvc.perform(get("/api/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(produtoSalvo.getId()))
                .andExpect(jsonPath("$.data.nome").value("X-Burger Teste"));
    }

    @Test
    void deveRemoverProdutoComSucesso() throws Exception {
        mockMvc.perform(delete("/api/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isNoContent()); // Espera 204 No Content
    }

    // Adicione mais testes para atualizar, alterar disponibilidade, buscar por categoria, buscar por nome, etc.
}