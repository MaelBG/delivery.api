package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor (para ErrorDetails)

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@Schema(description = "Estrutura padrão para respostas de erro da API")
public class ErrorResponse {

    @Schema(description = "Indica que a operação não foi bem-sucedida", example = "false")
    private boolean success = false;

    @Schema(description = "Detalhes do erro")
    private ErrorDetails error;

    @Schema(description = "Timestamp da resposta", example = "2024-01-15T10:30:00Z")
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, String details) {
        this.error = new ErrorDetails(code, message, details, null); // null para validationErrors
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String code, String message, Map<String, String> validationDetails) {
        this.error = new ErrorDetails(code, message, null, validationDetails); // null para details
        this.timestamp = LocalDateTime.now();
    }

    @Data // Gera getters, setters, toString, equals e hashCode para a classe aninhada
    @NoArgsConstructor // Construtor sem argumentos
    @AllArgsConstructor // Construtor com todos os argumentos
    @Schema(description = "Detalhes internos do erro")
    public static class ErrorDetails {
        @Schema(description = "Código do erro", example = "ENTITY_NOT_FOUND")
        private String code;
        @Schema(description = "Mensagem resumida do erro", example = "Restaurante não encontrado")
        private String message;
        @Schema(description = "Detalhes específicos do erro", example = "Nenhum restaurante encontrado com ID: 999", nullable = true)
        private String details;
        @Schema(description = "Detalhes de validação (se for um erro de validação)", nullable = true)
        private Map<String, String> validationErrors;
    }
}