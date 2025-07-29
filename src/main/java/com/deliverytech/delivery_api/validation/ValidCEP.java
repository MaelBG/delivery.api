package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CEPValidator.class) // [cite: 129]
@Target({ElementType.FIELD, ElementType.PARAMETER}) // [cite: 130]
@Retention(RetentionPolicy.RUNTIME) // [cite: 131]
public @interface ValidCEP {
    String message() default "CEP deve ter formato válido (00000-000 ou 00000000)"; // [cite: 133]
    Class<?>[] groups() default {}; // [cite: 134]
    Class<? extends Payload>[] payload() default {}; // [cite: 135]
}