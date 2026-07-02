package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorRequest {
    @NotBlank(message = "Author name is required")
    @Size(max = 150)
    private String name;

    private String biography;

    private LocalDate birthDate;
}
