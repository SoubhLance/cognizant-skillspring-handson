package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.AuthorDto;
import com.libraryManagementSystem.dto.AuthorRequest;
import com.libraryManagementSystem.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuthorDto>>> getAllAuthors() {
        List<AuthorDto> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(ApiResponse.success(authors, "Authors list fetched"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorDto>> getAuthorById(@PathVariable Long id) {
        AuthorDto author = authorService.getAuthorById(id);
        return ResponseEntity.ok(ApiResponse.success(author, "Author details fetched"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<AuthorDto>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        AuthorDto author = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(author, "Author created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<AuthorDto>> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        AuthorDto author = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(ApiResponse.success(author, "Author updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully"));
    }
}
