package com.libraryManagementSystem.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        // Min 8 chars, at least one uppercase, one lowercase, one digit, one special char
        boolean hasUppercase = value.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = value.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = value.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = value.chars().anyMatch(ch -> "!@#$%^&*()_+=-[]{}|;:',.<>/?~`".indexOf(ch) >= 0);
        boolean isLongEnough = value.length() >= 8;

        return isLongEnough && hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
