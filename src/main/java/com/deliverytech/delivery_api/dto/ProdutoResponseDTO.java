package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Schema(description = "Dados de resposta do produto")
public class ProdutoResponseDTO {

    @Schema(description = "ID do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza Calabresa")
    private String nome;

    @Schema(description = "Descrição do produto", example = "Molho de tomate, mussarela e calabresa fatiada")
    private String descricao;

    @Schema(description = "Preço do produto", example = "38.90")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "Pizza")
    private String categoria;

    @Schema(description = "Indica se o produto está disponível", example = "true")
    private boolean disponivel;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}