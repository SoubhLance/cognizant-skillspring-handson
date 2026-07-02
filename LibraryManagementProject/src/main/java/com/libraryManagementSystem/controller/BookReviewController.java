package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.BookReviewDto;
import com.libraryManagementSystem.dto.BookReviewRequest;
import com.libraryManagementSystem.service.BookReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class BookReviewController {

    @Autowired
    private BookReviewService bookReviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookReviewDto>> addReview(@Valid @RequestBody BookReviewRequest request) {
        BookReviewDto review = bookReviewService.addReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Review posted successfully"));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<BookReviewDto>>> getBookReviews(@PathVariable Long bookId) {
        List<BookReviewDto> reviews = bookReviewService.getBookReviews(bookId);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Book reviews fetched"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        bookReviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }
}
