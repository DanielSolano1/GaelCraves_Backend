package com.gaelcraves.project3.GaelCravings_Backend.Service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Custom Annotation
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordStrengthValidator.class)
public @interface StrongPassword {
    String message() default "Password does not meet strength requirements";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
