package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.deliverytech.delivery_api.validation.ValidTelefone;
import com.deliverytech.delivery_api.validation.ValidCategoria;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação ou atualização de um restaurante")
public class RestauranteDTO {

    @JsonProperty("nome")
    @Schema(description = "Nome do restaurante", example = "Pizza Express", required = true, minLength = 2, maxLength = 100)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @JsonProperty("categoria")
    @Schema(description = "Categoria do restaurante", example = "Italiana", required = true)
    @NotNull(message = "Categoria é obrigatória")
    @ValidCategoria
    private String categoria;

    @JsonProperty("endereco")
    @Schema(description = "Endereço completo do restaurante", example = "Rua das Flores, 123 - Centro", required = true)
    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @JsonProperty("telefone")
    @Schema(description = "Telefone para contato", example = "11999999999", required = true)
    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone
    private String telefone;

    @JsonProperty("taxaEntrega")
    @Schema(description = "Taxa de entrega em reais", example = "5.50", minimum = "0.01", required = true)
    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Taxa de entrega deve ser positiva")
    @DecimalMax(value = "50.0", message = "Taxa de entrega não pode exceder R$ 50,00")
    private BigDecimal taxaEntrega;

    @JsonProperty("tempoEntrega")
    @Schema(description = "Tempo estimado de entrega em minutos", example = "45", minimum = "10", maximum = "120", required = true)
    @NotNull(message = "Tempo de entrega é obrigatório")
    @Min(value = 10, message = "Tempo mínimo de entrega é 10 minutos")
    @Max(value = 120, message = "Tempo máximo de entrega é 120 minutos")
    private Integer tempoEntrega;

    @JsonProperty("horarioFuncionamento")
    @Schema(description = "Horário de funcionamento", example = "08:00-22:00", required = true)
    @NotBlank(message = "Horário de funcionamento é obrigatório")
    private String horarioFuncionamento;

    @JsonProperty("email")
    @Schema(description = "Email de contato do restaurante", example = "contato@restaurante.com", required = true)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;
}