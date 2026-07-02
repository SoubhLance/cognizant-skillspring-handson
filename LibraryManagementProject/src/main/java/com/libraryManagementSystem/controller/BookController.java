package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.BookDto;
import com.libraryManagementSystem.dto.BookRequest;
import com.libraryManagementSystem.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookRequest request) {
        BookDto book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        BookDto book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Book updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book, "Book details fetched"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookDto>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long publisherId,
            @RequestParam(required = false) Boolean available,
            Pageable pageable) {
        Page<BookDto> books = bookService.searchBooks(title, isbn, categoryId, authorId, publisherId, available, pageable);
        return ResponseEntity.ok(ApiResponse.success(books, "Books search result fetched"));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<BookDto>>> getRecommendations(@RequestParam Long categoryId) {
        List<BookDto> books = bookService.getRecommendations(categoryId);
        return ResponseEntity.ok(ApiResponse.success(books, "Recommendations fetched"));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(@RequestParam String query) {
        List<String> suggestions = bookService.getSearchSuggestions(query);
        return ResponseEntity.ok(ApiResponse.success(suggestions, "Search suggestions fetched"));
    }

    @PutMapping("/{id}/cover")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookDto>> updateCoverImage(@PathVariable Long id, @RequestParam String coverImageUrl) {
        BookDto book = bookService.updateCoverImage(id, coverImageUrl);
        return ResponseEntity.ok(ApiResponse.success(book, "Cover image updated successfully"));
    }
}
