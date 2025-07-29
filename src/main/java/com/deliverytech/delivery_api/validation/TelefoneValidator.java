package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> { // [cite: 176]

    @Override
    public void initialize(ValidTelefone constraintAnnotation) {
        // Inicialização se necessária [cite: 179]
    }

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) { // [cite: 182]
        if (telefone == null || telefone.trim().isEmpty()) { // [cite: 183]
            return false; // [cite: 185]
        }
        String cleanTelefone = telefone.replaceAll("[^\\d]", ""); // Remove caracteres não numéricos [cite: 187]
        return cleanTelefone.length() == 10 || cleanTelefone.length() == 11; // Verifica se tem 10 ou 11 dígitos [cite: 189]
    }
}