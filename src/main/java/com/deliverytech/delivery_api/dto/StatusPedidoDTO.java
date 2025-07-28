package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.entity.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Dados para atualização de status de um pedido")
public class StatusPedidoDTO {

    @Schema(description = "Novo status do pedido", example = "CONFIRMADO", required = true)
    @NotNull(message = "Status é obrigatório")
    private StatusPedido status;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}