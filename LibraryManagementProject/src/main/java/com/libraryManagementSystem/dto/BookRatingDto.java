package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRatingDto {
    private Long id;
    private Long bookId;
    private Long userId;
    private Integer ratingValue;
    private LocalDateTime createdAt;
}
