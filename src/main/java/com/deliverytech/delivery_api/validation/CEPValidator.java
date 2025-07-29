package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern; // [cite: 141]

public class CEPValidator implements ConstraintValidator<ValidCEP, String> { // [cite: 142]

    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$"); // [cite: 143, 144]

    @Override
    public void initialize(ValidCEP constraintAnnotation) {
        // Inicialização se necessária [cite: 147]
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) { // [cite: 150]
        if (cep == null || cep.trim().isEmpty()) { // [cite: 151]
            return false; // [cite: 152]
        }
        String cleanCep = cep.trim().replaceAll("\\s", ""); // [cite: 154]
        return CEP_PATTERN.matcher(cleanCep).matches(); // [cite: 155]
    }
}