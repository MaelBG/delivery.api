package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Dados para criação de um novo pedido")
public class PedidoDTO {

    @Schema(description = "ID do cliente que está fazendo o pedido", example = "1", required = true)
    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @Schema(description = "ID do restaurante onde o pedido será feito", example = "1", required = true)
    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;

    @Schema(description = "Endereço completo para entrega", example = "Rua do Cliente, 456 - Bairro", required = true)
    @NotBlank(message = "Endereço de entrega é obrigatório")
    private String enderecoEntrega;

    @Schema(description = "Observações adicionais para o pedido", example = "Sem cebola, por favor", nullable = true)
    private String observacoes;

    @Schema(description = "Lista de itens do pedido", required = true)
    @Size(min = 1, message = "Pedido deve conter pelo menos um item")
    @NotNull(message = "Itens do pedido são obrigatórios")
    private List<ItemPedidoDTO> itens;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}