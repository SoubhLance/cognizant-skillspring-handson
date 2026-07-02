package com.libraryManagementSystem.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsbnValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIsbn {
    String message() default "Invalid ISBN. Must be a valid 10 or 13-digit format.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
