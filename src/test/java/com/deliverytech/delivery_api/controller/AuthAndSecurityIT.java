package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.Login.LoginRequest;
import com.deliverytech.delivery_api.entity.Role;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthAndSecurityIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // --- ADIÇÕES IMPORTANTES ---
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Limpa o repositório para garantir que não haja dados de testes anteriores
        usuarioRepository.deleteAll();

        // Cria usuários de teste necessários para o login
        Usuario adminUser = new Usuario("admin@delivery.com", passwordEncoder.encode("123456"), "Admin Teste", Role.ADMIN);
        usuarioRepository.save(adminUser);

        Usuario clientUser = new Usuario("joao@email.com", passwordEncoder.encode("123456"), "Joao Teste", Role.CLIENTE);
        usuarioRepository.save(clientUser);
    }
    // --- FIM DAS ADIÇÕES ---

    private String obterTokenAdmin() throws Exception {
        return obterTokenParaUsuario("admin@delivery.com", "123456");
    }

    private String obterTokenCliente() throws Exception {
        return obterTokenParaUsuario("joao@email.com", "123456");
    }

    private String obterTokenParaUsuario(String email, String senha) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setSenha(senha);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Agora o login deve funcionar
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        // O token está dentro do objeto de resposta
        return objectMapper.readTree(responseContent).get("token").asText();
    }

    @Test
    void deveAcessarEndpointPublicoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/restaurantes"))
               .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401ParaEndpointProtegidoSemToken() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
               .andExpect(status().isUnauthorized()); // CORRIGIDO: Deve ser 401
    }

    @Test
    void deveAcessarEndpointDeAdminComTokenDeAdmin() throws Exception {
        String adminToken = obterTokenAdmin();
        // O endpoint /api/pedidos é de admin
        mockMvc.perform(get("/api/pedidos")
                .header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403ParaAcessoDeAdminComTokenDeCliente() throws Exception {
        String clientToken = obterTokenCliente();
        // Cliente não pode acessar a lista de todos os pedidos
        mockMvc.perform(get("/api/pedidos")
                .header("Authorization", "Bearer " + clientToken))
               .andExpect(status().isForbidden()); // CORRIGIDO: Deve ser 403
    }

    @Test
    void deveRetornar401ParaEndpointProtegidoComTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/pedidos")
                .header("Authorization", "Bearer token-invalido-qualquer-coisa"))
               .andExpect(status().isUnauthorized()); // CORRIGIDO: Deve ser 401
    }
}