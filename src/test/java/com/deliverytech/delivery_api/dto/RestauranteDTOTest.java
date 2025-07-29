package com.deliverytech.delivery_api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestauranteDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveValidarRestauranteDTOComDadosValidos() {
        RestauranteDTO dto = new RestauranteDTO(
                "Pizza Mania",
                "ITALIANA",
                "Rua A, 123",
                "11987654321",
                new BigDecimal("8.50"),
                30,
                "09:00-23:00",
                "contato@pizzamania.com"
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deveria haver violações para dados válidos");
    }

    @Test
    void naoDeveValidarRestauranteDTOComNomeVazio() {
        RestauranteDTO dto = new RestauranteDTO(
                "", // Nome vazio
                "ITALIANA",
                "Rua A, 123",
                "11987654321",
                new BigDecimal("8.50"),
                30,
                "09:00-23:00",
                "contato@pizzamania.com"
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Deve haver 1 violação");
        assertEquals("Nome é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void naoDeveValidarRestauranteDTOComTelefoneInvalido() {
        RestauranteDTO dto = new RestauranteDTO(
                "Pizza Mania",
                "ITALIANA",
                "Rua A, 123",
                "123", // Telefone inválido (menos de 10 dígitos)
                new BigDecimal("8.50"),
                30,
                "09:00-23:00",
                "contato@pizzamania.com"
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Deve haver 1 violação");
        assertEquals("Telefone deve ter formato válido (10 ou 11 dígitos)", violations.iterator().next().getMessage());
    }

    @Test
    void naoDeveValidarRestauranteDTOComTaxaEntregaNegativa() {
        RestauranteDTO dto = new RestauranteDTO(
                "Pizza Mania",
                "ITALIANA",
                "Rua A, 123",
                "11987654321",
                new BigDecimal("-2.00"), // Taxa de entrega negativa
                30,
                "09:00-23:00",
                "contato@pizzamania.com"
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Deve haver 1 violação");
        assertEquals("Taxa de entrega deve ser positiva", violations.iterator().next().getMessage());
    }

    @Test
    void naoDeveValidarRestauranteDTOComTempoEntregaForaDoIntervalo() {
        RestauranteDTO dto = new RestauranteDTO(
                "Pizza Mania",
                "ITALIANA",
                "Rua A, 123",
                "11987654321",
                new BigDecimal("8.50"),
                5, // Tempo de entrega menor que 10
                "09:00-23:00",
                "contato@pizzamania.com"
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Deve haver 1 violação");
        assertEquals("Tempo mínimo de entrega é 10 minutos", violations.iterator().next().getMessage());
    }

    @Test
    void naoDeveValidarRestauranteDTOComCategoriaInvalida() {
        RestauranteDTO dto = new RestauranteDTO(
                "Pizza Mania",
                "INVALIDA", // Categoria inválida
                "Rua A, 123",
                "11987654321",
                new BigDecimal("8.50"),
                30,
                "09:00-23:00",
                "contato@pizzamania.com"
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Deve haver 1 violação");
        assertEquals("Categoria deve ser uma das opções válidas", violations.iterator().next().getMessage());
    }

    @Test
    void naoDeveValidarRestauranteDTOComEmailInvalido() {
        RestauranteDTO dto = new RestauranteDTO(
                "Pizza Mania",
                "ITALIANA",
                "Rua A, 123",
                "11987654321",
                new BigDecimal("8.50"),
                30,
                "09:00-23:00",
                "email-invalido" // Email inválido
        );
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "Deve haver 1 violação");
        assertEquals("Email deve ter formato válido", violations.iterator().next().getMessage());
    }
}