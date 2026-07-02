package com.libraryManagementSystem.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        // Basic phone number validation: digits, optional plus, spaces, parentheses or dashes
        String clean = value.replaceAll("[\\s().-]", "");
        if (clean.startsWith("+")) {
            clean = clean.substring(1);
        }

        return clean.matches("\\d{7,15}");
    }
}
