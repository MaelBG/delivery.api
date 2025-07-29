package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern; // Para validar formaPagamento e CEP
import com.deliverytech.delivery_api.validation.ValidCEP; // Importe a anotação customizada
import jakarta.validation.Valid; // Para validar a lista de itens
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Size(max = 200, message = "Endereço não pode exceder 200 caracteres") // 
    private String enderecoEntrega;

    @Schema(description = "CEP do endereço de entrega", example = "12345-678", required = true)
    @NotBlank(message = "CEP é obrigatório") // [cite: 97]
    @ValidCEP // Aplica a validação customizada de CEP [cite: 98, 630]
    private String cep;

    @Schema(description = "Observações adicionais para o pedido", example = "Sem cebola, por favor", nullable = true)
    @Size(max = 500, message = "Observações não podem exceder 500 caracteres") // 
    private String observacoes;

    @Schema(description = "Forma de pagamento do pedido", example = "CARTAO_CREDITO", required = true)
    @NotBlank(message = "Forma de pagamento é obrigatória") // [cite: 102]
    @Pattern(regexp = "^(DINHEIRO|CARTAO_CREDITO|CARTAO_DEBITO|PIX)$", // [cite: 103, 104, 105, 106]
             message = "Forma de pagamento deve ser: DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO ou PIX")
    private String formaPagamento;

    @Schema(description = "Lista de itens do pedido", required = true)
    @Size(min = 1, message = "Pedido deve conter pelo menos um item") // 
    @NotNull(message = "Itens do pedido são obrigatórios")
    @Valid // Garante que os itens dentro da lista também sejam validados 
    private List<ItemPedidoDTO> itens;
}