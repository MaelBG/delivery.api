package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera construtor sem argumentos
@AllArgsConstructor // Gera construtor com todos os argumentos
@Schema(description = "Dados de resposta do restaurante")
public class RestauranteResponseDTO {

    @Schema(description = "ID do restaurante", example = "1")
    private Long id;

    @Schema(description = "Nome do restaurante", example = "Pizza Express")
    private String nome;

    @Schema(description = "Categoria do restaurante", example = "Italiana")
    private String categoria;

    @Schema(description = "Endereço do restaurante", example = "Rua das Flores, 123")
    private String endereco;

    @Schema(description = "Telefone do restaurante", example = "11999999999")
    private String telefone;

    @Schema(description = "Taxa de entrega", example = "5.50")
    private BigDecimal taxaEntrega;

    @Schema(description = "Avaliação do restaurante", example = "4.5")
    private BigDecimal avaliacao;

    @Schema(description = "Status de atividade do restaurante", example = "true")
    private boolean ativo;

    @Schema(description = "Tempo estimado de entrega em minutos", example = "45")
    private Integer tempoEntrega;

    @Schema(description = "Horário de funcionamento", example = "08:00-22:00")
    private String horarioFuncionamento;

    // Getters, setters, construtores, etc., são gerados automaticamente pelo Lombok
}