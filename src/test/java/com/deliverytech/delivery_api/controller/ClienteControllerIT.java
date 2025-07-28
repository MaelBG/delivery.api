package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository; // Adicionado para deleteAll em setup
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

import java.time.LocalDateTime; // Se usado para dataCadastro no setup
import java.util.List; // Se usado para Listar Clientes

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PedidoRepository pedidoRepository; // <-- ADICIONADO

    private Cliente clienteDTO; // Usando a entidade diretamente como DTO para teste
    private Cliente clienteSalvo;

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();

        // Cliente para cadastro
        clienteDTO = new Cliente();
        clienteDTO.setNome("Novo Cliente");
        clienteDTO.setEmail("novo@cliente.com");
        clienteDTO.setTelefone("11900001111");
        clienteDTO.setEndereco("Rua Nova, 10");

        // Cliente para busca/atualização/inativação
        Cliente clienteBase = new Cliente();
        clienteBase.setNome("Cliente Existente");
        clienteBase.setEmail("existente@cliente.com");
        clienteBase.setTelefone("11911112222");
        clienteBase.setEndereco("Av. Existente, 20");
        clienteBase.setAtivo(true);
        clienteSalvo = clienteRepository.save(clienteBase);
    }

    @Test
    void deveCadastrarClienteComSucesso() throws Exception {
        mockMvc.perform(post("/clientes") // Note: o ClienteController original não usa /api/
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Cliente")) // O ClienteController original retorna a entidade diretamente, não ApiResponseWrapper
                .andExpect(jsonPath("$.email").value("novo@cliente.com"));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        mockMvc.perform(get("/clientes/{id}", clienteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteSalvo.getId()))
                .andExpect(jsonPath("$.nome").value("Cliente Existente"));
    }

    @Test
    void deveInativarClienteComSucesso() throws Exception {
        mockMvc.perform(delete("/clientes/{id}", clienteSalvo.getId()))
                .andExpect(status().isOk()) // Espera 200 OK (com mensagem "Cliente inativado com sucesso")
                .andExpect(content().string("Cliente inativado com sucesso"));
    }

    // Adicione mais testes para:
    // - Listar clientes
    // - Atualizar cliente
    // - Buscar por nome/email
    // - Cenários de erro (email duplicado, dados inválidos, IDs não encontrados)
}