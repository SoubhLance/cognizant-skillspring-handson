package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String barcode;
    private String status;
    private String bookCondition;
}
