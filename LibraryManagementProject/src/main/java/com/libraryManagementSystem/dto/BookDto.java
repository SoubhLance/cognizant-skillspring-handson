package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private Long categoryId;
    private String categoryName;
    private Long publisherId;
    private String publisherName;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private Integer totalCopies;
    private Integer availableCopies;
    private Set<AuthorDto> authors;
    private LocalDateTime createdAt;
}
