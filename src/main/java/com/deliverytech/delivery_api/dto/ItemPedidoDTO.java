package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Dados de um item do pedido")
public class ItemPedidoDTO {

    @Schema(description = "ID do produto", example = "1", required = true)
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;

    @Schema(description = "Quantidade do produto", example = "1", minimum = "1", required = true)
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    private Integer quantidade;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}