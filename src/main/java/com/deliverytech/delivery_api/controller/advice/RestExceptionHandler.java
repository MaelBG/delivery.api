package com.deliverytech.delivery_api.controller.advice;

import com.deliverytech.delivery_api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Indica que esta classe lida com exceções em todos os controladores
public class RestExceptionHandler {

    // Manipula erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Retorna 400 Bad Request
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Retorna o ErrorResponse com os detalhes de validação
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "Erro de validação", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manipula exceções IllegalArgumentException (como "Restaurante não encontrado")
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Ou HttpStatus.NOT_FOUND dependendo do contexto
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Se a mensagem contiver "não encontrado", podemos retornar 404
        if (ex.getMessage().contains("não encontrado")) {
            ErrorResponse errorResponse = new ErrorResponse("ENTITY_NOT_FOUND", ex.getMessage(), ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // Retorna 404
        } else {
            ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", ex.getMessage(), ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Retorna 400
        }
    }

    // Manipulador genérico para outras exceções não tratadas (erro 500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Retorna 500 Internal Server Error
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Ocorreu um erro interno no servidor.",
                ex.getMessage() != null ? ex.getMessage() : "Detalhes adicionais não disponíveis."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}