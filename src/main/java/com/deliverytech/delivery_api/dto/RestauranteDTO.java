package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.deliverytech.delivery_api.validation.ValidTelefone; // Importar a anotação customizada [cite: 27]
import com.deliverytech.delivery_api.validation.ValidCategoria; // Importar a anotação customizada [cite: 28]

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastro e atualização de restaurante")
public class RestauranteDTO {

    @JsonProperty("nome")
    @Schema(description = "Nome do restaurante", example = "Pizza Express", required = true)
    @NotBlank(message = "Nome é obrigatório") // [cite: 30, 619]
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") // [cite: 31, 619]
    private String nome;

    @JsonProperty("categoria")
    @Schema(description = "Categoria do restaurante", example = "Italiana") // [cite: 35, 619]
    @NotNull(message = "Categoria é obrigatória") // [cite: 33]
    @ValidCategoria // Aplica a validação customizada de categoria [cite: 34, 633]
    private String categoria;

    @JsonProperty("endereco")
    @Schema(description = "Endereço completo do restaurante", example = "Rua das Flores, 123 - Centro")
    @NotBlank(message = "Endereço é obrigatório") // [cite: 45, 628]
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres") // [cite: 46]
    private String endereco;

    @JsonProperty("telefone")
    @Schema(description = "Telefone para contato", example = "11999999999")
    @NotBlank(message = "Telefone é obrigatório") // [cite: 36, 619]
    @ValidTelefone // Aplica a validação customizada de telefone [cite: 37, 631]
    private String telefone;

    @JsonProperty("taxaEntrega")
    @Schema(description = "Taxa de entrega em reais", example = "5.50", minimum = "0")
    @NotNull(message = "Taxa de entrega é obrigatória") // [cite: 39, 619]
    @DecimalMin(value = "0.0", inclusive = false, message = "Taxa de entrega deve ser positiva") // [cite: 40, 619]
    @DecimalMax(value = "50.0", message = "Taxa de entrega não pode exceder R$ 50,00") // [cite: 41]
    private BigDecimal taxaEntrega;

    @JsonProperty("tempoEntrega")
    @Schema(description = "Tempo estimado de entrega em minutos", example = "45",
            minimum = "10", maximum = "120")
    @NotNull(message = "Tempo de entrega é obrigatório") // [cite: 43]
    @Min(value = 10, message = "Tempo mínimo de entrega é 10 minutos") // [cite: 44, 620]
    @Max(value = 120, message = "Tempo máximo de entrega é 120 minutos") // [cite: 44, 620]
    private Integer tempoEntrega;

    @JsonProperty("horarioFuncionamento")
    @Schema(description = "Horário de funcionamento", example = "08:00-22:00")
    @NotBlank(message = "Horário de funcionamento é obrigatório")
    // @ValidHorarioFuncionamento // Se for implementada a validação customizada
    private String horarioFuncionamento;

    @JsonProperty("email") // Adicionado conforme o roteiro 
    @Schema(description = "Email de contato do restaurante", example = "contato@restaurante.com", required = true)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido") // 
    private String email;
}