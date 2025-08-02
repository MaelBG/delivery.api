package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.ProdutoDTO;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários Finais do ProdutoService")
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private RestauranteRepository restauranteRepository; // Necessário para alguns testes

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private Restaurante restaurante;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setup() {
        restaurante = new Restaurante();
        restaurante.setId(1L);

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Produto Teste");
        produto.setPreco(BigDecimal.TEN);
        produto.setRestaurante(restaurante);
        
        produtoDTO = new ProdutoDTO("Nome Válido", "Desc", BigDecimal.TEN, "Cat", 1L, null, true);
    }

    // --- TESTES PARA OS MÉTODOS EM VERMELHO E AMARELO ---

    @Test
    @DisplayName("Deve retornar true se o usuário logado for dono do produto")
    void deveRetornarTrueParaIsOwner() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));

        // Mocka a chamada estática para SecurityUtils, uma técnica avançada do Mockito
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentRestauranteld).thenReturn(1L);
            
            assertTrue(produtoService.isOwner(10L));
        }
    }
    
    @Test
    @DisplayName("Deve retornar false se o usuário logado NÃO for dono do produto")
    void deveRetornarFalseParaIsNotOwner() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentRestauranteld).thenReturn(99L); // ID diferente
            
            assertFalse(produtoService.isOwner(10L));
        }
    }

    @Test
    @DisplayName("Deve buscar apenas produtos disponíveis do restaurante")
    void deveBuscarProdutosDisponiveis() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        produtoService.buscarProdutosPorRestaurante(1L, true);
        verify(produtoRepository).findByRestauranteIdAndDisponivelTrue(1L);
    }

    @Test
    @DisplayName("Deve buscar apenas produtos indisponíveis do restaurante")
    void deveBuscarProdutosIndisponiveis() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        produtoService.buscarProdutosPorRestaurante(1L, false);
        verify(produtoRepository).findByRestauranteIdAndDisponivelFalse(1L);
    }

    @Test
    @DisplayName("Deve buscar todos os produtos quando 'disponivel' é nulo")
    void deveBuscarTodosOsProdutos() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        produtoService.buscarProdutosPorRestaurante(1L, null);
        verify(produtoRepository).findByRestauranteId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar produto com nome vazio")
    void deveLancarExcecaoParaNomeVazio() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        produtoDTO.setNome(""); // Nome inválido
        assertThrows(BusinessException.class, () -> produtoService.atualizarProduto(10L, produtoDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar produto com preço zero")
    void deveLancarExcecaoParaPrecoZero() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        produtoDTO.setPreco(BigDecimal.ZERO); // Preço inválido
        assertThrows(BusinessException.class, () -> produtoService.atualizarProduto(10L, produtoDTO));
    }
}