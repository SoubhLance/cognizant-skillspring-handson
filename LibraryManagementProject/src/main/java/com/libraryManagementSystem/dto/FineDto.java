package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineDto {
    private Long id;
    private Long issueId;
    private String bookTitle;
    private String userName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentDate;
    private String transactionId;
}
