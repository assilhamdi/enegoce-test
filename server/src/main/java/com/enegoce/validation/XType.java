package com.enegoce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = XValidator.class)
public @interface XType {
    String message() default "Invalid X type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
