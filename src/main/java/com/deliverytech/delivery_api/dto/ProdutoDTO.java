package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @DecimalMax(value = "500.00", message = "Preço não pode exceder R$ 500,00") // [cite: 61, 622]
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "Pizza")
    @NotBlank(message = "Categoria é obrigatória")
    // @ValidCategoria // Usar anotação customizada se implementada (ver abaixo)
    private String categoria;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1", required = true)
    @NotNull(message = "Restaurante ID é obrigatório") // [cite: 68]
    @Positive(message = "Restaurante ID deve ser positivo") // [cite: 69]
    private Long restauranteId; // Corrigido para restauranteId conforme o DTO

    @Schema(description = "URL da imagem do produto", example = "http://example.com/pizza.jpg", nullable = true)
    @Pattern(regexp = "^(https?://).*\\.(jpg|jpeg|png|gif)$", // 
             message = "URL da imagem deve ser válida e ter formato JPG, JPEG, PNG ou GIF")
    private String imagemUrl;

    @Schema(description = "Indica se o produto está disponível por padrão", example = "true")
    @AssertTrue(message = "Produto deve estar disponível por padrão") // 
    private Boolean disponivel = true;
}