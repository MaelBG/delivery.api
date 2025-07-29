package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefoneValidator.class) // [cite: 164]
@Target({ElementType.FIELD, ElementType.PARAMETER}) // [cite: 165]
@Retention(RetentionPolicy.RUNTIME) // [cite: 166]
public @interface ValidTelefone {
    String message() default "Telefone deve ter formato válido (10 ou 11 dígitos)"; // [cite: 168]
    Class<?>[] groups() default {}; // [cite: 170]
    Class<? extends Payload>[] payload() default {}; // [cite: 171]
}