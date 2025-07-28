package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Resultado do cálculo do total de um pedido")
public class CalculoPedidoResponseDTO {

    @Schema(description = "Subtotal dos itens do pedido", example = "50.00")
    private BigDecimal subtotal;

    @Schema(description = "Taxa de entrega aplicada", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Valor total calculado (subtotal + taxa de entrega)", example = "55.00")
    private BigDecimal valorTotal;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}