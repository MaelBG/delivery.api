package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.RestauranteDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO; // Necessário para testes com ApiResponseWrapper
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository; // Adicionado para deleteAll em setup
import com.deliverytech.delivery_api.repository.ProdutoRepository; // Adicionado para deleteAll em setup
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

import java.math.BigDecimal; // Se usado no DTO ou entidade
import java.util.List; // Se usado para listas (e.g., hasSize)

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest // [cite: 718]
@AutoConfigureMockMvc // [cite: 719] - Usamos AutoConfigureMockMvc para testes com MockMvc
@ActiveProfiles("test") // [cite: 720]
@Transactional // [cite: 721]
class RestauranteControllerIT { // [cite: 722]

    @Autowired
    private MockMvc mockMvc; // [cite: 724]
    @Autowired
    private ObjectMapper objectMapper; // [cite: 726]
    @Autowired
    private RestauranteRepository restauranteRepository; // [cite: 728]
 // --- ADIÇÃO DOS @AUTOWIRED FALTANTES ---
    @Autowired
    private PedidoRepository pedidoRepository; // <-- ADICIONADO
    @Autowired
    private ProdutoRepository produtoRepository; // <-- ADICIONADO
    @Autowired
    private ClienteRepository clienteRepository; // <-- ADICIONADO
    // --- FIM DA ADIÇÃO ---


    private RestauranteDTO restauranteDTO; // [cite: 729]
    private Restaurante restauranteSalvo; // [cite: 730]

    @BeforeEach
    void setup() {
        // Excluir entidades na ordem inversa de suas dependências (filhos antes dos pais)
        pedidoRepository.deleteAll();    // Pedido depende de Cliente e Restaurante
        produtoRepository.deleteAll();   // Produto depende de Restaurante
        // Se houver mais entidades que dependam de Restaurante ou Cliente, delete-as aqui
        restauranteRepository.deleteAll(); // Restaurante pode ser excluído agora
        clienteRepository.deleteAll();   // Cliente pode ser excluído agora
        // ... restante do seu setup ...
    

        restauranteDTO = new RestauranteDTO(); // [cite: 734]
        restauranteDTO.setNome("Pizza Express"); // [cite: 735]
        restauranteDTO.setCategoria("Italiana"); // [cite: 736]
        restauranteDTO.setEndereco("Rua das Flores, 123"); // [cite: 737]
        restauranteDTO.setTelefone("11999999999"); // [cite: 738]
        restauranteDTO.setTaxaEntrega(new BigDecimal("5.50")); // [cite: 739]
        restauranteDTO.setTempoEntrega(45); // [cite: 740]
        restauranteDTO.setHorarioFuncionamento("08:00-22:00"); // [cite: 741]

        // Criar restaurante para testes de busca/atualização
        Restaurante restauranteBase = new Restaurante(); // [cite: 744]
        restauranteBase.setNome("Burger King"); // [cite: 745]
        restauranteBase.setCategoria("Americana"); // [cite: 746]
        restauranteBase.setEndereco("Av. Paulista, 1000"); // [cite: 747]
        restauranteBase.setTelefone("11888888888"); // [cite: 748]
        restauranteBase.setTaxaEntrega(new BigDecimal("4.00")); // [cite: 749]
        restauranteBase.setTempoEntrega(30); // [cite: 750]
        restauranteBase.setHorarioFuncionamento("10:00-23:00"); // [cite: 751]
        restauranteBase.setAvaliacao(new BigDecimal("4.0")); // Campo da sua entidade
        restauranteBase.setAtivo(true); // [cite: 752]

        restauranteSalvo = restauranteRepository.save(restauranteBase); // [cite: 753]
    }

    @Test // [cite: 754]
    void deveCadastrarRestauranteComSucesso() throws Exception {
        mockMvc.perform(post("/api/restaurantes") // [cite: 756]
                .contentType(MediaType.APPLICATION_JSON) // [cite: 757]
                .content(objectMapper.writeValueAsString(restauranteDTO))) // [cite: 758]
                .andExpect(status().isCreated()) // [cite: 760]
                .andExpect(jsonPath("$.success").value(true)) // [cite: 761]
                .andExpect(jsonPath("$.data.nome").value("Pizza Express")) // [cite: 762]
                .andExpect(jsonPath("$.data.categoria").value("Italiana")) // [cite: 763]
                .andExpect(jsonPath("$.data.ativo").value(true)) // [cite: 764]
                .andExpect(jsonPath("$.message").value("Restaurante criado com sucesso")); // [cite: 765]
    }

    @Test // [cite: 766]
    void deveRejeitarRestauranteComDadosInvalidos() throws Exception {
        restauranteDTO.setNome(""); // [cite: 768]
        restauranteDTO.setTelefone("123"); // [cite: 769]

        mockMvc.perform(post("/api/restaurantes") // [cite: 770]
                .contentType(MediaType.APPLICATION_JSON) // [cite: 771]
                .content(objectMapper.writeValueAsString(restauranteDTO))) // [cite: 772]
                .andExpect(status().isBadRequest()) // [cite: 773]
                .andExpect(jsonPath("$.success").value(false)) // [cite: 774]
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR")) // [cite: 775]
                .andExpect(jsonPath("$.error.details.nome").exists()) // [cite: 776]
                .andExpect(jsonPath("$.error.details.telefone").exists()); // [cite: 777]
    }

    @Test // [cite: 779]
    void deveBuscarRestaurantePorId() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", restauranteSalvo.getId())) // [cite: 781]
                .andExpect(status().isOk()) // [cite: 782]
                .andExpect(jsonPath("$.success").value(true)) // [cite: 783]
                .andExpect(jsonPath("$.data.id").value(restauranteSalvo.getId())) // [cite: 784]
                .andExpect(jsonPath("$.data.nome").value("Burger King")) // [cite: 785]
                .andExpect(jsonPath("$.data.categoria").value("Americana")); // [cite: 786]
    }

    @Test // [cite: 788]
    void deveRetornar404ParaRestauranteInexistente() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", 999L)) // [cite: 790]
                .andExpect(status().isNotFound()) // [cite: 791]
                .andExpect(jsonPath("$.success").value(false)) // [cite: 792]
                .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND")); // [cite: 793]
    }

    @Test // [cite: 795]
    void deveListarRestaurantesComPaginacao() throws Exception {
        mockMvc.perform(get("/api/restaurantes") // [cite: 797]
                .param("page", "0") // [cite: 798]
                .param("size", "10")) // [cite: 799]
                .andExpect(status().isOk()) // [cite: 800]
                .andExpect(jsonPath("$.content").isArray()) // [cite: 801]
                .andExpect(jsonPath("$.content", hasSize(1))) // [cite: 802]
                .andExpect(jsonPath("$.page.number").value(0)) // [cite: 802]
                .andExpect(jsonPath("$.page.size").value(10)) // [cite: 802]
                .andExpect(jsonPath("$.page.totalElements").value(1)); // [cite: 803]
    }

    @Test // [cite: 805]
    void deveAtualizarRestauranteComSucesso() throws Exception {
        restauranteDTO.setNome("Pizza Express Atualizada"); // [cite: 806]

        mockMvc.perform(put("/api/restaurantes/{id}", restauranteSalvo.getId()) // [cite: 807]
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restauranteDTO)))
                .andExpect(status().isOk()) // [cite: 810]
                .andExpect(jsonPath("$.success").value(true)) // [cite: 812]
                .andExpect(jsonPath("$.data.nome").value("Pizza Express Atualizada")) // [cite: 813]
                .andExpect(jsonPath("$.message").value("Restaurante atualizado com sucesso")); // [cite: 814]
    }

    @Test // [cite: 815]
    void deveAlterarStatusRestaurante() throws Exception {
        mockMvc.perform(patch("/api/restaurantes/{id}/status", restauranteSalvo.getId())) // [cite: 817]
                .andExpect(status().isOk()) // [cite: 818]
                .andExpect(jsonPath("$.success").value(true)) // [cite: 819]
                .andExpect(jsonPath("$.data.ativo").value(false)) // [cite: 820]
                .andExpect(jsonPath("$.message").value("Status alterado com sucesso")); // [cite: 821]
    }

    @Test // [cite: 823]
    void deveBuscarRestaurantesPorCategoria() throws Exception {
        mockMvc.perform(get("/api/restaurantes/categoria/{categoria}", "Americana")) // [cite: 825]
                .andExpect(status().isOk()) // [cite: 826]
                .andExpect(jsonPath("$.success").value(true)) // [cite: 827]
                .andExpect(jsonPath("$.data").isArray()) // [cite: 828]
                .andExpect(jsonPath("$.data", hasSize(1))) // [cite: 830]
                .andExpect(jsonPath("$.data[0].categoria").value("Americana")); // [cite: 831]
    }
}