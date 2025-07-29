package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CategoriaValidator.class) // [cite: 198]
@Target({ElementType.FIELD, ElementType.PARAMETER}) // [cite: 199]
@Retention(RetentionPolicy.RUNTIME) // [cite: 200]
public @interface ValidCategoria {
    String message() default "Categoria deve ser uma das opções válidas"; // [cite: 202]
    Class<?>[] groups() default {}; // [cite: 203]
    Class<? extends Payload>[] payload() default {}; // [cite: 204]
}