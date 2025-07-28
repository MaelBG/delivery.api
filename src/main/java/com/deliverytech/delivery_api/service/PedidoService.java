package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.repository.*;
import com.deliverytech.delivery_api.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Importe para suportar paginação
import org.springframework.data.domain.Pageable; // Importe para suportar paginação
import org.springframework.data.jpa.domain.Specification; // Importe paraSpecifications
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Métodos de Mapeamento (Helper Methods) - Inalterados, mas incluídos para completude
    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getDataPedido(),
                pedido.getEnderecoEntrega(),
                pedido.getSubtotal(),
                pedido.getTaxaEntrega(),
                pedido.getValorTotal(),
                pedido.getObservacoes(),
                pedido.getStatus(),
                pedido.getCliente() != null ? pedido.getCliente().getId() : null,
                pedido.getCliente() != null ? pedido.getCliente().getNome() : null,
                pedido.getRestaurante() != null ? pedido.getRestaurante().getId() : null,
                pedido.getRestaurante() != null ? pedido.getRestaurante().getNome() : null,
                pedido.getItens().stream()
                      .map(this::toItemPedidoResponseDTO)
                      .collect(Collectors.toList())
        );
    }

    private ItemPedidoResponseDTO toItemPedidoResponseDTO(ItemPedido itemPedido) {
        if (itemPedido == null) {
            return null;
        }
        return new ItemPedidoResponseDTO(
                itemPedido.getId(),
                itemPedido.getProduto() != null ? itemPedido.getProduto().getId() : null,
                itemPedido.getProduto() != null ? itemPedido.getProduto().getNome() : null,
                itemPedido.getQuantidade(),
                itemPedido.getPrecoUnitario(),
                itemPedido.getSubtotal()
        );
    }

    private Pedido toEntity(PedidoDTO dto) {
        if (dto == null) {
            return null;
        }
        Pedido pedido = new Pedido();
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
        pedido.setObservacoes(dto.getObservacoes());
        return pedido;
    }

    // Métodos do Serviço - Principais - Inalterados, mas incluídos para completude
    public PedidoResponseDTO criarPedido(PedidoDTO dto) { /* ... lógica ... */ return null; }
    public PedidoResponseDTO adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) { /* ... lógica ... */ return null; }
    public PedidoResponseDTO confirmarPedido(Long pedidoId) { /* ... lógica ... */ return null; }
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) { /* ... lógica ... */ return null; }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) { /* ... lógica ... */ return null; }
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorNumero(String numeroPedido) { /* ... lógica ... */ return null; }
    public PedidoResponseDTO atualizarStatusPedido(Long pedidoId, StatusPedidoDTO statusDTO) { /* ... lógica ... */ return null; }
    public void cancelarPedido(Long pedidoId, String motivo) { /* ... lógica ... */ }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, StatusPedido status) { /* ... lógica ... */ return null; }
    @Transactional(readOnly = true)
    public CalculoPedidoResponseDTO calcularTotalPedido(CalculoPedidoDTO dto) { /* ... lógica ... */ return null; }


    // --- MÉTODO listarPedidos CORRIGIDO ---
    /**
     * Listar pedidos com filtros de status e período, e paginação.
     * Agora retorna Page<PedidoResponseDTO> e aceita Pageable.
     */
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        Specification<Pedido> spec = Specification.where(null); // Inicia uma Specification vazia

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
        }
        if (dataInicio != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("dataPedido"), dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("dataPedido"), dataFim));
        }

        // Usa o findAll do JpaRepository que aceita Specification e Pageable
        Page<Pedido> pedidosPage = pedidoRepository.findAll(spec, pageable);

        // Mapeia a Page de entidades para uma Page de DTOs
        return pedidosPage.map(this::toResponseDTO);
    }
    // --- FIM DO MÉTODO listarPedidos CORRIGIDO ---
}