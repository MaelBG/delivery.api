package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Dados para cálculo do total de um pedido (sem salvá-lo)")
public class CalculoPedidoDTO {

    @Schema(description = "ID do restaurante para cálculo da taxa de entrega", example = "1", required = true)
    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;

    @Schema(description = "Lista de itens a serem considerados no cálculo", required = true)
    @Size(min = 1, message = "Deve conter pelo menos um item para cálculo")
    @NotNull(message = "Itens são obrigatórios")
    private List<ItemCalculoDTO> itens;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}