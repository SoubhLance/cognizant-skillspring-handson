package com.libraryManagementSystem.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Let NotNull handle null checks

        // Remove dashes and spaces
        String clean = value.replaceAll("[-\\s]", "");
        
        if (clean.length() != 10 && clean.length() != 13) {
            return false;
        }

        // Basic check for digits
        return clean.matches("\\d+x?") || clean.matches("\\d+X?") || clean.matches("\\d+");
    }
}
