package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.repository.*;
import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.exception.EntityNotFoundException; // Importar EntityNotFoundException
import com.deliverytech.delivery_api.exception.BusinessException; // Importar BusinessException (para validações gerais)
import com.deliverytech.delivery_api.exception.ConflictException; // Importar ConflictException

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Para gerar numeroPedido único
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

    // Métodos de Mapeamento (Helper Methods)
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
        // CEP e Forma de Pagamento não são mapeados diretamente para a entidade Pedido
        // A lógica de negócio pode usar esses campos do DTO diretamente
        return pedido;
    }

    // Métodos do Serviço - Principais
    public PedidoResponseDTO criarPedido(PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente", dto.getClienteId()));

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", dto.getRestauranteId()));

        if (!restaurante.isAtivo()) {
            throw new BusinessException("Restaurante inativo, não é possível fazer pedidos.", "RESTAURANT_INACTIVE");
        }
        
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
        pedido.setObservacoes(dto.getObservacoes());
        pedido.setNumeroPedido("PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTaxaEntrega(restaurante.getTaxaEntrega());

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new BusinessException("Pedido deve conter pelo menos um item.", "ORDER_EMPTY");
        }

        for (ItemPedidoDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto", itemDto.getProdutoId()));

            if (!produto.isDisponivel()) {
                throw new BusinessException("Produto " + produto.getNome() + " não está disponível.", "PRODUCT_UNAVAILABLE");
            }
            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("Produto " + produto.getNome() + " não pertence a este restaurante.", "PRODUCT_MISMATCH");
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itemPedido.calcularSubtotal();
            pedido.adicionarItem(itemPedido);
        }

        pedido.calcularTotais();
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return toResponseDTO(pedidoSalvo);
    }

    public PedidoResponseDTO adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", pedidoId));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto", produtoId));

        if (!produto.isDisponivel()) {
            throw new BusinessException("Produto " + produto.getNome() + " não está disponível.", "PRODUCT_UNAVAILABLE");
        }
        if (!produto.getRestaurante().getId().equals(pedido.getRestaurante().getId())) {
            throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante do pedido.", "PRODUCT_MISMATCH");
        }
        
        // Verificar se o pedido já está em um status que não permite modificação
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Não é possível adicionar itens a um pedido que não esteja PENDENTE.", "ORDER_STATUS_INVALID");
        }

        ItemPedido itemExistente = pedido.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
            itemExistente.calcularSubtotal();
        } else {
            ItemPedido novoItem = new ItemPedido();
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantidade);
            novoItem.setPrecoUnitario(produto.getPreco());
            novoItem.calcularSubtotal();
            pedido.adicionarItem(novoItem);
        }
        
        pedido.calcularTotais();
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    public PedidoResponseDTO confirmarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", pedidoId));

        if (pedido.getItens().isEmpty()) {
            throw new BusinessException("Pedido deve ter pelo menos um item para ser confirmado.", "ORDER_EMPTY");
        }
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Pedido não está no status PENDENTE para ser confirmado.", "ORDER_STATUS_INVALID");
        }

        pedido.confirmar(); // Altera o status para CONFIRMADO
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));
        return toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", clienteId));
        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
        return pedidos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorNumero(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido);
        if (pedido == null) {
            throw new EntityNotFoundException("Pedido com número " + numeroPedido + " não encontrado.", null); // Não há ID específico para o erro
        }
        return toResponseDTO(pedido);
    }

    public PedidoResponseDTO atualizarStatusPedido(Long pedidoId, StatusPedidoDTO statusDTO) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", pedidoId));

        StatusPedido novoStatus = statusDTO.getStatus();
        StatusPedido currentStatus = pedido.getStatus();

        // Lógica de transição de status (exemplo simplificado)
        if (novoStatus == StatusPedido.CANCELADO) {
             if (currentStatus == StatusPedido.ENTREGUE) {
                throw new BusinessException("Pedido entregue não pode ser cancelado.", "INVALID_STATUS_TRANSITION");
            }
        } else if (novoStatus == StatusPedido.CONFIRMADO) {
            if (currentStatus != StatusPedido.PENDENTE) {
                throw new BusinessException("Pedido só pode ser confirmado se estiver PENDENTE.", "INVALID_STATUS_TRANSITION");
            }
        } else if (novoStatus == StatusPedido.PREPARANDO) {
            if (currentStatus != StatusPedido.CONFIRMADO) {
                throw new BusinessException("Pedido só pode ir para PREPARANDO se estiver CONFIRMADO.", "INVALID_STATUS_TRANSITION");
            }
        } else if (novoStatus == StatusPedido.SAIU_PARA_ENTREGA) {
            if (currentStatus != StatusPedido.PREPARANDO) {
                throw new BusinessException("Pedido só pode SAIR_PARA_ENTREGA se estiver PREPARANDO.", "INVALID_STATUS_TRANSITION");
            }
        } else if (novoStatus == StatusPedido.ENTREGUE) {
            if (currentStatus != StatusPedido.SAIU_PARA_ENTREGA) {
                throw new BusinessException("Pedido só pode ser ENTREGUE se estiver SAIU_PARA_ENTREGA.", "INVALID_STATUS_TRANSITION");
            }
        } else if (novoStatus == StatusPedido.PENDENTE && currentStatus != StatusPedido.CANCELADO) {
            // Permitir voltar para pendente apenas se foi cancelado, para reativar
            throw new BusinessException("Não é possível alterar o status para PENDENTE a partir de " + currentStatus, "INVALID_STATUS_TRANSITION");
        }

        pedido.setStatus(novoStatus);
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    public void cancelarPedido(Long pedidoId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", pedidoId));

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Pedido já foi entregue e não pode ser cancelado.", "ORDER_ALREADY_DELIVERED");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já está cancelado.", "ORDER_ALREADY_CANCELLED");
        }
        
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setObservacoes(pedido.getObservacoes() != null ? pedido.getObservacoes() + " | Cancelado: " + motivo : "Cancelado: " + motivo);
        pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, StatusPedido status) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", restauranteId));

        List<Pedido> pedidos;
        if (status != null) {
            // Combina a busca por restaurante e status
            pedidos = pedidoRepository.relatorioPedidosPorPeriodoEStatus(LocalDateTime.MIN, LocalDateTime.MAX, status) // Usando relatorioPedidosPorPeriodoEStatus de forma adaptada
                                    .stream()
                                    .filter(p -> p.getRestaurante().getId().equals(restauranteId)) // Filtra por restaurante
                                    .collect(Collectors.toList());
        } else {
            pedidos = pedidoRepository.findByRestauranteId(restauranteId);
        }
        return pedidos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CalculoPedidoResponseDTO calcularTotalPedido(CalculoPedidoDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", dto.getRestauranteId()));

        BigDecimal subtotal = BigDecimal.ZERO;
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new BusinessException("Deve conter pelo menos um item para cálculo.", "CALCULATION_EMPTY_ITEMS");
        }

        for (ItemCalculoDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto", itemDto.getProdutoId()));
            
            if (!produto.isDisponivel()) {
                throw new BusinessException("Produto " + produto.getNome() + " não está disponível para cálculo.", "PRODUCT_UNAVAILABLE_FOR_CALC");
            }

            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal itemSubtotal = precoUnitario.multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal taxaEntrega = restaurante.getTaxaEntrega() != null ? restaurante.getTaxaEntrega() : BigDecimal.ZERO;
        BigDecimal valorTotal = subtotal.add(taxaEntrega);

        return new CalculoPedidoResponseDTO(subtotal, taxaEntrega, valorTotal);
    }

    // Método listarPedidos corrigido (já estava no código fornecido)
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        Specification<Pedido> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
        }
        if (dataInicio != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("dataPedido"), dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("dataPedido"), dataFim));
        }

        Page<Pedido> pedidosPage = pedidoRepository.findAll(spec, pageable);
        return pedidosPage.map(this::toResponseDTO);
    }
}