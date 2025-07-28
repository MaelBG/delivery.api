package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Detalhes de um item de pedido na resposta")
public class ItemPedidoResponseDTO {

    @Schema(description = "ID do item de pedido", example = "1")
    private Long id;

    @Schema(description = "ID do produto", example = "1")
    private Long produtoId;

    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String produtoNome;

    @Schema(description = "Quantidade do produto neste item", example = "1")
    private Integer quantidade;

    @Schema(description = "Preço unitário do produto no momento do pedido", example = "35.90")
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal deste item (quantidade * preço unitário)", example = "35.90")
    private BigDecimal subtotal;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}