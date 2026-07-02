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
public class WishlistDto {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String coverImageUrl;
    private String isbn;
    private LocalDateTime addedAt;
}
