package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookCopyRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotBlank(message = "Barcode is required")
    private String barcode;

    private String status; // Optional, defaults to AVAILABLE

    private String bookCondition; // Optional, defaults to Good
}
