package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookReturnRequest {
    @NotBlank(message = "Book copy barcode is required")
    private String barcode;

    @NotBlank(message = "Return condition is required")
    private String condition; // e.g. Good, Damaged, Lost
}
