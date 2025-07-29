package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size; // Importe para a validação de tamanho
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de um item do pedido")
public class ItemPedidoDTO {

    @Schema(description = "ID do produto", example = "1", required = true)
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;

    @Schema(description = "Quantidade do produto", example = "1", minimum = "1", required = true)
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    private Integer quantidade;

    @Schema(description = "Observações específicas para este item do pedido", example = "Sem picles", nullable = true)
    @Size(max = 200, message = "Observações não podem exceder 200 caracteres") // 
    private String observacoes;
}