package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookIssueDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long copyId;
    private String copyBarcode;
    private String bookTitle;
    private String isbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private LocalDateTime createdAt;
}
