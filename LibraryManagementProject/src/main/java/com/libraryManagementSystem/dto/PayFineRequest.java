package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayFineRequest {
    @NotNull(message = "Fine ID is required")
    private Long fineId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
}
