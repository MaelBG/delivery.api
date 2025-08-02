package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.StatusPedidoDTO;
import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários Definitivos e Completos do PedidoService")
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private Produto produto;
    private Restaurante restaurante;

    @BeforeEach
    void setup() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));

        produto = new Produto();
        produto.setId(10L);
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);
        produto.setPreco(new BigDecimal("20.00"));

        pedido = new Pedido();
        pedido.setId(100L);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setRestaurante(restaurante);
        pedido.setItens(new ArrayList<>());
    }

    // --- Testes Abrangentes para Adicionar Item ---

    @Test
    @DisplayName("Deve atualizar a quantidade de um item existente no pedido")
    void deveAtualizarQuantidadeItemExistente() {
        ItemPedido itemExistente = new ItemPedido();
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(1);
        pedido.getItens().add(itemExistente);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.adicionarItem(100L, 10L, 2);

        assertEquals(1, pedido.getItens().size());
        assertEquals(3, pedido.getItens().get(0).getQuantidade());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao adicionar produto de outro restaurante")
    void deveLancarExcecaoAoAdicionarProdutoDeOutroRestaurante() {
        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(2L);
        produto.setRestaurante(outroRestaurante);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));

        assertThrows(BusinessException.class, () -> pedidoService.adicionarItem(100L, 10L, 1));
    }
    
    // --- Testes Abrangentes para Confirmar Pedido ---

    @Test
    @DisplayName("Deve lançar exceção ao tentar confirmar pedido sem itens")
    void deveLancarExcecaoAoConfirmarPedidoVazio() {
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        assertThrows(BusinessException.class, () -> pedidoService.confirmarPedido(100L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar pedido que não está pendente")
    void deveLancarExcecaoAoConfirmarPedidoNaoPendente() {
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedido.getItens().add(new ItemPedido()); // Adiciona item para passar da primeira validação
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        assertThrows(BusinessException.class, () -> pedidoService.confirmarPedido(100L));
    }
    
    // --- Testes Abrangentes para Atualizar Status ---

    @Test
    @DisplayName("Deve testar todas as transições de status inválidas")
    void deveLancarExcecoesParaTodasTransicoesInvalidas() {
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

        pedido.setStatus(StatusPedido.PENDENTE);
        assertThrows(BusinessException.class, () -> pedidoService.atualizarStatusPedido(100L, new StatusPedidoDTO(StatusPedido.PREPARANDO)));

        pedido.setStatus(StatusPedido.CONFIRMADO);
        assertThrows(BusinessException.class, () -> pedidoService.atualizarStatusPedido(100L, new StatusPedidoDTO(StatusPedido.SAIU_PARA_ENTREGA)));
        
        pedido.setStatus(StatusPedido.PREPARANDO);
        assertThrows(BusinessException.class, () -> pedidoService.atualizarStatusPedido(100L, new StatusPedidoDTO(StatusPedido.ENTREGUE)));

        pedido.setStatus(StatusPedido.ENTREGUE); // Um pedido entregue não pode ser cancelado por esta rota
        assertThrows(BusinessException.class, () -> pedidoService.atualizarStatusPedido(100L, new StatusPedidoDTO(StatusPedido.CANCELADO)));
    }
    
    // --- Testes Abrangentes para Cancelar Pedido ---

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar um pedido já cancelado")
    void deveLancarExcecaoAoCancelarPedidoJaCancelado() {
        pedido.setStatus(StatusPedido.CANCELADO);
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> pedidoService.cancelarPedido(100L, "Tentar de novo"));
    }

    // --- Testes Abrangentes para Listar e Buscar ---

    @Test
    @DisplayName("Deve listar pedidos com filtros de data e status")
    void deveListarPedidosComFiltros() {
        Page<Pedido> page = new PageImpl<>(Collections.singletonList(pedido));
        when(pedidoRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        PageRequest pageable = PageRequest.of(0, 10);
        var resultado = pedidoService.listarPedidos(StatusPedido.PENDENTE, LocalDateTime.now().minusDays(1), LocalDateTime.now(), pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    // --- Testes Abrangentes para canAccess (método de segurança) ---

    @Test
    @DisplayName("canAccess deve retornar true para restaurante dono do pedido")
    void deveRetornarTrueParaRestauranteDono() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentRestauranteld).thenReturn(1L);
            assertTrue(pedidoService.canAccess(1L, "RESTAURANTE"));
        }
    }

    @Test
    @DisplayName("canAccess deve retornar false para restaurante não dono")
    void deveRetornarFalseParaRestauranteNaoDono() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentRestauranteld).thenReturn(99L);
            assertFalse(pedidoService.canAccess(1L, "RESTAURANTE"));
        }
    }
}