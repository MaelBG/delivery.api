package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PedidoRepository pedidoRepository;

    private Cliente clienteExistente;

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        clienteExistente = new Cliente();
        clienteExistente.setNome("Cliente Existente");
        clienteExistente.setEmail("existente@cliente.com");
        clienteExistente.setAtivo(true);
        clienteRepository.save(clienteExistente);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarCliente() throws Exception {
        Cliente novoCliente = new Cliente();
        novoCliente.setNome("Novo Cliente");
        novoCliente.setEmail("novo@cliente.com");
        mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(novoCliente)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void deveListarClientes() throws Exception {
        mockMvc.perform(get("/clientes")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveBuscarClientePorId() throws Exception {
        mockMvc.perform(get("/clientes/{id}", clienteExistente.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarCliente() throws Exception {
        clienteExistente.setNome("Nome Atualizado");
        mockMvc.perform(put("/clientes/{id}", clienteExistente.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(clienteExistente)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveInativarCliente() throws Exception {
        mockMvc.perform(delete("/clientes/{id}", clienteExistente.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveBuscarPorNome() throws Exception {
        mockMvc.perform(get("/clientes/buscar").param("nome", "Existente")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveBuscarPorEmail() throws Exception {
        mockMvc.perform(get("/clientes/email/{email}", "existente@cliente.com")).andExpect(status().isOk());
    }
}