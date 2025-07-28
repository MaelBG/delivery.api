package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Dados para cadastro e atualização de produto")
public class ProdutoDTO {

    @Schema(description = "Nome do produto", example = "Pizza Calabresa", required = true)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "Descrição detalhada do produto", example = "Molho de tomate, mussarela e calabresa fatiada")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @Schema(description = "Preço do produto", example = "38.90", minimum = "0.01", required = true)
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "Pizza")
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}