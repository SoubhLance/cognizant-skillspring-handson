package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookIssueRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Book copy barcode is required")
    private String barcode;

    private LocalDate dueDate; // If null, auto-calculated based on user role limits
}
