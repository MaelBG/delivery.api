package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.Login.RegisterRequest;
import com.deliverytech.delivery_api.entity.Role;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do AuthService")
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    // --- TESTES EXISTENTES ---

    @Test
    @DisplayName("Deve carregar usuário pelo email com sucesso")
    void should_LoadUserByUsername_When_UserExists() {
        Usuario usuario = new Usuario("test@email.com", "senha", "Teste", Role.CLIENTE);
        when(usuarioRepository.findByEmailAndAtivo("test@email.com", true)).thenReturn(Optional.of(usuario));

        UserDetails userDetails = authService.loadUserByUsername("test@email.com");

        assertNotNull(userDetails);
        assertEquals("test@email.com", userDetails.getUsername());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void should_ThrowException_When_UserNotFound() {
        when(usuarioRepository.findByEmailAndAtivo(anyString(), any(Boolean.class))).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername("notfound@email.com"));
    }

    // --- NOVOS TESTES PARA COBRIR OS MÉTODOS EM VERMELHO ---

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso")
    void deveCriarUsuario() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNome("Novo Usuario");
        request.setEmail("novo@email.com");
        request.setSenha("123456");
        request.setRole(Role.CLIENTE);

        when(passwordEncoder.encode("123456")).thenReturn("senha_criptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario novoUsuario = authService.criarUsuario(request);

        // Then
        assertNotNull(novoUsuario);
        assertEquals("Novo Usuario", novoUsuario.getNome());
        assertEquals("novo@email.com", novoUsuario.getEmail());
        assertEquals("senha_criptografada", novoUsuario.getSenha());
        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve retornar true quando email existe")
    void deveVerificarSeEmailExiste() {
        // Given
        when(usuarioRepository.existsByEmail("existente@email.com")).thenReturn(true);

        // When
        boolean resultado = authService.existsByEmail("existente@email.com");

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void deveVerificarSeEmailNaoExiste() {
        // Given
        when(usuarioRepository.existsByEmail("naoexiste@email.com")).thenReturn(false);

        // When
        boolean resultado = authService.existsByEmail("naoexiste@email.com");

        // Then
        assertFalse(resultado);
    }
}