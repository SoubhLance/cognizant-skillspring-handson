package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.BookDto;
import com.libraryManagementSystem.dto.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookDto createBook(BookRequest request);
    BookDto updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
    BookDto getBookById(Long id);
    BookDto getBookByIsbn(String isbn);
    Page<BookDto> searchBooks(String title, String isbn, Long categoryId, Long authorId, Long publisherId, Boolean available, Pageable pageable);
    List<BookDto> getRecommendations(Long categoryId);
    List<String> getSearchSuggestions(String query);
    BookDto updateCoverImage(Long id, String coverImageUrl);
}
