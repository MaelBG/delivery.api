package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.entity.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor (para construtor completo)

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
// @AllArgsConstructor // Descomente se precisar de um construtor com todos os campos (Lombok geraria um)
@Schema(description = "Dados de resposta de um pedido")
public class PedidoResponseDTO {

    @Schema(description = "ID do pedido", example = "1")
    private Long id;

    @Schema(description = "Número único do pedido", example = "PED123456789")
    private String numeroPedido;

    @Schema(description = "Data e hora do pedido", example = "2024-07-21T15:30:00")
    private LocalDateTime dataPedido;

    @Schema(description = "Endereço de entrega do pedido", example = "Rua A, 123 - São Paulo/SP")
    private String enderecoEntrega;

    @Schema(description = "Subtotal dos itens do pedido", example = "50.00")
    private BigDecimal subtotal;

    @Schema(description = "Taxa de entrega aplicada ao pedido", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Valor total do pedido (subtotal + taxa de entrega)", example = "55.00")
    private BigDecimal valorTotal;

    @Schema(description = "Observações do pedido", example = "Sem cebola", nullable = true)
    private String observacoes;

    @Schema(description = "Status atual do pedido", example = "PENDENTE")
    private StatusPedido status;

    @Schema(description = "ID do cliente que fez o pedido", example = "1")
    private Long clienteId;

    @Schema(description = "Nome do cliente que fez o pedido", example = "João Silva")
    private String clienteNome;

    @Schema(description = "ID do restaurante do pedido", example = "1")
    private Long restauranteId;

    @Schema(description = "Nome do restaurante do pedido", example = "Pizzaria Bella")
    private String restauranteNome;

    @Schema(description = "Lista de itens detalhados do pedido")
    private List<ItemPedidoResponseDTO> itens;

    // Construtor manual se precisar de uma lógica específica de mapeamento, ou para @AllArgsConstructor
    public PedidoResponseDTO(Long id, String numeroPedido, LocalDateTime dataPedido, String enderecoEntrega,
                             BigDecimal subtotal, BigDecimal taxaEntrega, BigDecimal valorTotal,
                             String observacoes, StatusPedido status, Long clienteId, String clienteNome,
                             Long restauranteId, String restauranteNome, List<ItemPedidoResponseDTO> itens) {
        this.id = id;
        this.numeroPedido = numeroPedido;
        this.dataPedido = dataPedido;
        this.enderecoEntrega = enderecoEntrega;
        this.subtotal = subtotal;
        this.taxaEntrega = taxaEntrega;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.status = status;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.restauranteId = restauranteId;
        this.restauranteNome = restauranteNome;
        this.itens = itens;
    }
    // Getters, setters, etc., são gerados automaticamente pelo Lombok
}