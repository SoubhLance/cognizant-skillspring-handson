package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class BookRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 20)
    private String isbn;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Publisher ID is required")
    private Long publisherId;

    private Integer publicationYear;

    private String description;

    private String coverImageUrl;

    @NotNull(message = "Author IDs are required")
    private Set<Long> authorIds;

    @Min(value = 0, message = "Total copies cannot be negative")
    private Integer totalCopies;
}
