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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// CORREÇÃO DEFINITIVA: Garante que o contexto do Spring seja recriado, limpando o banco
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthAndSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String userEmail = "user@test.com";
    private String userPassword = "password";

    @BeforeEach
    void setup() {
        // Agora, com @DirtiesContext, este setup roda em um ambiente limpo
        Usuario user = new Usuario(userEmail, passwordEncoder.encode(userPassword), "Test User", Role.CLIENTE);
        usuarioRepository.save(user);
    }

    private String obterToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userEmail);
        loginRequest.setSenha(userPassword);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseContent).get("token").asText();
    }

    @Test
    void deveBuscarUsuarioLogadoComTokenValido() throws Exception {
        // Passo 1: Obter um token JWT real fazendo login
        String token = obterToken();

        // Passo 2: Usar o token para acessar o endpoint protegido /api/auth/me
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail));
    }
}