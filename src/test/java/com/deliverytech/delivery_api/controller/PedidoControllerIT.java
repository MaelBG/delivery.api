package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ItemPedidoDTO;
import com.deliverytech.delivery_api.dto.PedidoDTO;
import com.deliverytech.delivery_api.dto.StatusPedidoDTO;
import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.entity.StatusPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    private Cliente clienteTeste;
    private Restaurante restauranteTeste;
    private Produto produtoTeste1;
    private Produto produtoTeste2;
    private PedidoDTO pedidoDTO; // DTO para criar pedidos
    private PedidoDTO pedidoSalvoDTO; // DTO para referenciar um pedido já salvo

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        clienteRepository.deleteAll();

        // Criar entidades de base para o teste
        clienteTeste = new Cliente();
        clienteTeste.setNome("Cliente Teste");
        clienteTeste.setEmail("cliente@test.com");
        clienteTeste.setTelefone("11987654321");
        clienteTeste.setEndereco("Rua do Teste, 100");
        clienteTeste.setAtivo(true);
        clienteTeste = clienteRepository.save(clienteTeste);

        restauranteTeste = new Restaurante();
        restauranteTeste.setNome("Restaurante Teste");
        restauranteTeste.setCategoria("Teste");
        restauranteTeste.setEndereco("Av. Principal, 500");
        restauranteTeste.setTelefone("11111111111");
        restauranteTeste.setTaxaEntrega(new BigDecimal("5.00"));
        restauranteTeste.setAtivo(true);
        restauranteTeste = restauranteRepository.save(restauranteTeste);

        produtoTeste1 = new Produto();
        produtoTeste1.setNome("Item Teste 1");
        produtoTeste1.setDescricao("Descrição item 1");
        produtoTeste1.setPreco(new BigDecimal("10.00"));
        produtoTeste1.setCategoria("Categoria Teste");
        produtoTeste1.setDisponivel(true);
        produtoTeste1.setRestaurante(restauranteTeste);
        produtoTeste1 = produtoRepository.save(produtoTeste1);

        produtoTeste2 = new Produto();
        produtoTeste2.setNome("Item Teste 2");
        produtoTeste2.setDescricao("Descrição item 2");
        produtoTeste2.setPreco(new BigDecimal("15.00"));
        produtoTeste2.setCategoria("Categoria Teste");
        produtoTeste2.setDisponivel(true);
        produtoTeste2.setRestaurante(restauranteTeste);
        produtoTeste2 = produtoRepository.save(produtoTeste2);

        // Criar um PedidoDTO para os testes
        ItemPedidoDTO item1 = new ItemPedidoDTO(produtoTeste1.getId(), 1);
        ItemPedidoDTO item2 = new ItemPedidoDTO(produtoTeste2.getId(), 2);
        List<ItemPedidoDTO> itens = Arrays.asList(item1, item2);

        pedidoDTO = new PedidoDTO(clienteTeste.getId(), restauranteTeste.getId(),
                clienteTeste.getEndereco(), "Observações de teste", itens);
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.clienteId").value(clienteTeste.getId()))
                .andExpect(jsonPath("$.data.restauranteId").value(restauranteTeste.getId()))
                .andExpect(jsonPath("$.data.status").value("PENDENTE"))
                .andExpect(jsonPath("$.data.itens", hasSize(2)));
    }

    @Test
    void deveAtualizarStatusPedidoParaConfirmado() throws Exception {
        // Primeiro, crie um pedido para poder atualizar seu status
        String jsonPedidoCriado = mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long pedidoId = objectMapper.readTree(jsonPedidoCriado).get("data").get("id").asLong();

        StatusPedidoDTO statusDTO = new StatusPedidoDTO(StatusPedido.CONFIRMADO);

        mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(pedidoId))
                .andExpect(jsonPath("$.data.status").value("CONFIRMADO"));
    }

    // Adicione mais testes para:
    // - Buscar pedido por ID
    // - Cancelar pedido
    // - Listar pedidos (com filtros e paginação)
    // - Calcular total (POST /api/pedidos/calcular)
    // - Cenários de erro (dados inválidos, IDs não encontrados, transições de status inválidas)
}