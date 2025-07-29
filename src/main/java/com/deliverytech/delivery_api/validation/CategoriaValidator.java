package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays; // [cite: 210]
import java.util.List; // [cite: 211]

public class CategoriaValidator implements ConstraintValidator<ValidCategoria, String> { // [cite: 212]

    private static final List<String> CATEGORIAS_VALIDAS = Arrays.asList( // [cite: 213]
            "BRASILEIRA", "ITALIANA", "JAPONESA", "CHINESA", "MEXICANA",
            "FAST_FOOD", "PIZZA", "HAMBURGUER", "SAUDAVEL",
            "VEGETARIANA", "VEGANA", "DOCES", "BEBIDAS", "LANCHES", "ACAI"
    );

    @Override
    public void initialize(ValidCategoria constraintAnnotation) {
        // Inicialização se necessária [cite: 221]
    }

    @Override
    public boolean isValid(String categoria, ConstraintValidatorContext context) { // [cite: 224]
        if (categoria == null || categoria.trim().isEmpty()) { // [cite: 225]
            return false; // [cite: 226]
        }
        return CATEGORIAS_VALIDAS.contains(categoria.toUpperCase()); // [cite: 228]
    }
}