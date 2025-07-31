package com.deliverytech.delivery_api.controller.advice;

import com.deliverytech.delivery_api.dto.ErrorResponse;
import com.deliverytech.delivery_api.exception.BusinessException; // Importar BusinessException
import com.deliverytech.delivery_api.exception.EntityNotFoundException; // Importar EntityNotFoundException
import com.deliverytech.delivery_api.exception.ConflictException; // Importar ConflictException
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // 
public class RestExceptionHandler {

    // Manipula erros de validação (@Valid) [cite: 649]
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Retorna 400 Bad Request [cite: 348, 671]
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // [cite: 337]
        ex.getBindingResult().getAllErrors().forEach((error) -> { // [cite: 343]
            String fieldName = ((FieldError) error).getField(); // [cite: 345]
            String errorMessage = error.getDefaultMessage(); // [cite: 345]
            errors.put(fieldName, errorMessage); // [cite: 345]
        });
        // Retorna o ErrorResponse com os detalhes de validação
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "Erro de validação nos dados enviados", errors); // [cite: 350, 352]
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // [cite: 354, 355]
    }

    // Manipula EntityNotFoundException (retorna 404 Not Found) [cite: 650, 672]
    @ExceptionHandler(EntityNotFoundException.class) // [cite: 357]
    @ResponseStatus(HttpStatus.NOT_FOUND) // [cite: 368]
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) { // [cite: 358, 359]
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getMessage()); // [cite: 365, 367]
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // [cite: 368]
    }

    // Manipula ConflictException (retorna 409 Conflict) [cite: 652, 673]
    @ExceptionHandler(ConflictException.class) // [cite: 369]
    @ResponseStatus(HttpStatus.CONFLICT) // [cite: 385]
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) { // [cite: 370, 371]
        Map<String, String> details = new HashMap<>(); // [cite: 372]
        if (ex.getConflictField() != null) { // [cite: 373]
            details.put(ex.getConflictField(), ex.getConflictValue() != null ? ex.getConflictValue().toString() : "N/A"); // [cite: 375]
        }
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage(), details); // [cite: 381, 383, 384]
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // [cite: 385]
    }

    // Manipula BusinessException (retorna 400 Bad Request) [cite: 651, 671]
    @ExceptionHandler(BusinessException.class) // Isso deve vir antes da Exception genérica
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        // Usa o errorCode e a mensagem da BusinessException
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode() != null ? ex.getErrorCode() : "BUSINESS_ERROR",
                ex.getMessage(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // --- CÓDIGO A SER ADICIONADO ---
    /**
     * Manipula exceções de acesso negado (erro 403 Forbidden)
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "ACCESS_DENIED",
                "Acesso negado. Você não tem permissão para executar esta ação.",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Manipulador genérico para outras exceções não tratadas (erro 500) [cite: 653, 675]
    @ExceptionHandler(Exception.class) // [cite: 386]
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // [cite: 395, 396]
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) { // [cite: 387, 388]
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR", // [cite: 394]
                "Ocorreu um erro interno no servidor.", // [cite: 392]
                ex.getMessage() != null ? ex.getMessage() : "Detalhes adicionais não disponíveis."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // [cite: 395, 396]
    }
}