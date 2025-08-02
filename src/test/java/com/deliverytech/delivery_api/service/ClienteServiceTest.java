package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.exception.ConflictException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o uso de anotações do Mockito
@DisplayName("Testes Unitários do ClienteService") // Nomeia a suíte de testes
class ClienteServiceTest {

    @Mock // Cria um mock (simulação) do repositório
    private ClienteRepository clienteRepository;

    @InjectMocks // Injeta os mocks (clienteRepository) na classe que está sendo testada
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach // Método executado antes de cada teste para preparar os dados
    void setup() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setAtivo(true);
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando os dados são válidos") //
    void should_CadastrarCliente_When_DadosValidos() {
        // Given (Dado)
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false); // Simula que o email não existe
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente); // Simula o salvamento

        // When (Quando)
        Cliente clienteSalvo = clienteService.cadastrar(cliente);

        // Then (Então)
        assertNotNull(clienteSalvo); // Verifica se o resultado não é nulo
        assertEquals("João Silva", clienteSalvo.getNome()); // Verifica se o nome está correto
        assertTrue(clienteSalvo.isAtivo());
        verify(clienteRepository).existsByEmail("joao@email.com"); // Verifica se o método existsByEmail foi chamado
        verify(clienteRepository).save(cliente); // Verifica se o método save foi chamado
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar cadastrar email que já existe") //
    void should_ThrowConflictException_When_EmailJaExiste() {
        // Given
        when(clienteRepository.existsByEmail("joao@email.com")).thenReturn(true); // Simula que o email já existe

        // When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> clienteService.cadastrar(cliente)
        );

        assertEquals("Email já cadastrado: joao@email.com", exception.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class)); // Garante que o método save nunca foi chamado
    }

    @Test
    @DisplayName("Deve retornar um cliente quando o ID existir") //
    void should_ReturnCliente_When_IdExiste() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente)); // Simula o retorno de um cliente

        // When
        Optional<Cliente> resultado = clienteService.buscarPorId(1L);

        // Then
        assertTrue(resultado.isPresent()); // Verifica se o Optional não está vazio
        assertEquals("João Silva", resultado.get().getNome());
        verify(clienteRepository).findById(1L); // Verifica se o findById foi chamado
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o ID não existir") //
    void should_ReturnEmpty_When_IdNaoExiste() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty()); // Simula o retorno de um Optional vazio

        // When
        Optional<Cliente> resultado = clienteService.buscarPorId(99L);

        // Then
        assertFalse(resultado.isPresent()); // Verifica se o Optional está vazio
        verify(clienteRepository).findById(99L); //
    }

    @Test
    @DisplayName("Deve inativar um cliente com sucesso")
    void should_InativarCliente_When_ClienteExiste() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        clienteService.inativar(1L);

        // Then
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(cliente);
        assertFalse(cliente.isAtivo());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar inativar cliente inexistente")
    void should_ThrowEntityNotFoundException_When_InativarClienteNaoExistente() {
        // Given
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.inativar(99L);
        });

        verify(clienteRepository, never()).save(any()); // Garante que save não foi chamado
    }
}