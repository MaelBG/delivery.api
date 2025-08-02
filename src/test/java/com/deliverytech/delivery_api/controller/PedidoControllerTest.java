package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.CalculoPedidoDTO;
import com.deliverytech.delivery_api.dto.ItemCalculoDTO;
import com.deliverytech.delivery_api.dto.ItemPedidoDTO;
import com.deliverytech.delivery_api.dto.PedidoDTO;
import com.deliverytech.delivery_api.dto.StatusPedidoDTO;
import com.deliverytech.delivery_api.entity.*;
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
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ProdutoRepository produtoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto1;
    private Pedido pedidoSalvo;

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        clienteRepository.deleteAll();
        restauranteRepository.deleteAll();

        cliente = clienteRepository.save(new Cliente());
        restaurante = new Restaurante();
        restaurante.setAtivo(true);
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));
        restaurante = restauranteRepository.save(restaurante);

        produto1 = new Produto();
        produto1.setRestaurante(restaurante);
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setDisponivel(true);
        produto1 = produtoRepository.save(produto1);

        pedidoSalvo = new Pedido();
        pedidoSalvo.setCliente(cliente);
        pedidoSalvo.setRestaurante(restaurante);
        pedidoSalvo.setStatus(StatusPedido.PENDENTE);
        pedidoSalvo.setNumeroPedido("PED-12345");
        pedidoRepository.save(pedidoSalvo);
    }

    // --- TESTES QUE VOCÊ JÁ TINHA ---

    @Test
    @DisplayName("Deve criar um pedido")
    @WithMockUser(roles = "CLIENTE")
    void deveCriarPedido() throws Exception {
        ItemPedidoDTO itemDTO = new ItemPedidoDTO(produto1.getId(), 2, null);
        PedidoDTO pedidoDTO = new PedidoDTO(cliente.getId(), restaurante.getId(), "Endereço", "12345-678", "Obs", "PIX", Collections.singletonList(itemDTO));
        mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve buscar um pedido por ID")
    @WithMockUser
    void deveBuscarPedidoPorId() throws Exception {
        mockMvc.perform(get("/api/pedidos/{id}", pedidoSalvo.getId())).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve confirmar um pedido")
    @WithMockUser(roles = "CLIENTE")
    void deveConfirmarPedido() throws Exception {
        ItemPedido itemValido = new ItemPedido();
        itemValido.setProduto(produto1);
        itemValido.setQuantidade(1);
        itemValido.setPrecoUnitario(produto1.getPreco());
        itemValido.calcularSubtotal();
        pedidoSalvo.adicionarItem(itemValido);
        pedidoRepository.save(pedidoSalvo);
        mockMvc.perform(put("/api/pedidos/{pedidoId}/confirmar", pedidoSalvo.getId())).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve atualizar o status de um pedido")
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarStatus() throws Exception {
        StatusPedidoDTO statusDTO = new StatusPedidoDTO(StatusPedido.CONFIRMADO);
        mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoSalvo.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk());
    }

    // --- NOVOS TESTES PARA COBRIR OS MÉTODOS EM VERMELHO ---

    @Test
    @DisplayName("Deve listar todos os pedidos")
    @WithMockUser(roles = "ADMIN")
    void deveListarPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve buscar pedido por número")
    @WithMockUser
    void deveBuscarPedidoPorNumero() throws Exception {
        mockMvc.perform(get("/api/pedidos/numero/{numeroPedido}", pedidoSalvo.getNumeroPedido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(pedidoSalvo.getId()));
    }
    
    @Test
    @DisplayName("Deve buscar pedidos por cliente")
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPedidosPorCliente() throws Exception {
        mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    @DisplayName("Deve buscar pedidos por restaurante")
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPedidosPorRestaurante() throws Exception {
        mockMvc.perform(get("/api/pedidos/restaurante/{restauranteId}", restaurante.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    @DisplayName("Deve calcular o total de um pedido")
    @WithMockUser
    void deveCalcularTotal() throws Exception {
        List<ItemCalculoDTO> itens = Collections.singletonList(new ItemCalculoDTO(produto1.getId(), 3));
        CalculoPedidoDTO calculoDTO = new CalculoPedidoDTO(restaurante.getId(), itens);

        mockMvc.perform(post("/api/pedidos/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valorTotal").value(35.00)); // 3 * 10.00 + 5.00
    }
}