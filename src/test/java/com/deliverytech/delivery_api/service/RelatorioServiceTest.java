package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários Finais do RelatorioService")
public class RelatorioServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @Test
    @DisplayName("Deve chamar o repositório para obter vendas por restaurante")
    void deveObterVendasPorRestaurante() {
        // Given
        when(restauranteRepository.relatorioVendasPorRestaurante()).thenReturn(Collections.emptyList());

        // When
        relatorioService.getVendasPorRestaurante();

        // Then
        verify(restauranteRepository).relatorioVendasPorRestaurante();
    }

    @Test
    @DisplayName("Deve chamar o repositório para obter produtos mais vendidos")
    void deveObterProdutosMaisVendidos() {
        // Given
        when(produtoRepository.findProdutosMaisVendidos()).thenReturn(Collections.emptyList());

        // When
        relatorioService.getProdutosMaisVendidos();

        // Then
        verify(produtoRepository).findProdutosMaisVendidos();
    }

    @Test
    @DisplayName("Deve chamar o repositório para obter clientes mais ativos")
    void deveObterClientesMaisAtivos() {
        // Given
        when(clienteRepository.rankingClientesPorPedidos()).thenReturn(Collections.emptyList());

        // When
        relatorioService.getClientesMaisAtivos();

        // Then
        verify(clienteRepository).rankingClientesPorPedidos();
    }

    @Test
    @DisplayName("Deve chamar o repositório para obter pedidos por período")
    void deveObterPedidosPorPeriodo() {
        // Given
        when(pedidoRepository.countPedidosByStatus()).thenReturn(Collections.emptyList());
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        // When
        relatorioService.getPedidosPorPeriodo(inicio, fim);

        // Then
        verify(pedidoRepository).countPedidosByStatus();
    }
}