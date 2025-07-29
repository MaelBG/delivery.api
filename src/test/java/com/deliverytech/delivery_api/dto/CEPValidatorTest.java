package com.deliverytech.delivery_api.dto; // Ou o pacote onde você criou seu CEPValidatorTest

import com.deliverytech.delivery_api.validation.CEPValidator; // <--- ADICIONE ESTA LINHA
import jakarta.validation.ConstraintValidatorContext; // Você pode precisar desta importação se usar 'null' como contexto diretamente
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CEPValidatorTest {

    private CEPValidator cepValidator;

    @BeforeEach
    void setUp() {
        cepValidator = new CEPValidator();
        // Não é necessário chamar initialize para testes unitários simples
    }

    @Test
    void deveRetornarTrueParaCEPValidoComHifen() {
        // Use um mock para ConstraintValidatorContext se precisar, ou passe null se o validador lida com isso.
        // Já que seu CEPValidator aceita null para o contexto, podemos manter assim.
        assertTrue(cepValidator.isValid("12345-678", null), "CEP com hífen deve ser válido");
    }

    @Test
    void deveRetornarTrueParaCEPValidoSemHifen() {
        assertTrue(cepValidator.isValid("12345678", null), "CEP sem hífen deve ser válido");
    }

    @Test
    void deveRetornarFalseParaCEPNulo() {
        assertFalse(cepValidator.isValid(null, null), "CEP nulo deve ser inválido");
    }

    @Test
    void deveRetornarFalseParaCEPVazio() {
        assertFalse(cepValidator.isValid("", null), "CEP vazio deve ser inválido");
    }

    @Test
    void deveRetornarFalseParaCEPComCaracteresInvalidos() {
        assertFalse(cepValidator.isValid("12345-abc", null), "CEP com caracteres inválidos deve ser inválido");
    }

    @Test
    void deveRetornarFalseParaCEPComTamanhoIncorreto() {
        assertFalse(cepValidator.isValid("12345-67", null), "CEP com tamanho incorreto deve ser inválido");
        assertFalse(cepValidator.isValid("123456789", null), "CEP com tamanho incorreto deve ser inválido");
    }
}