package com.gaelcraves.project3.GaelCravings_Backend.Service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Constraint Validator
public class PasswordStrengthValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false; // Or handle as desired
        }

        // Example: Check for minimum length, uppercase, lowercase, digit, and special character
        boolean hasMinimumLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");

        return hasMinimumLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}