package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.BookRatingDto;
import com.libraryManagementSystem.dto.BookRatingRequest;
import com.libraryManagementSystem.service.BookRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class BookRatingController {

    @Autowired
    private BookRatingService bookRatingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookRatingDto>> addRating(@Valid @RequestBody BookRatingRequest request) {
        BookRatingDto rating = bookRatingService.addRating(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rating, "Rating updated successfully"));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<BookRatingDto>>> getBookRatings(@PathVariable Long bookId) {
        List<BookRatingDto> list = bookRatingService.getBookRatings(bookId);
        return ResponseEntity.ok(ApiResponse.success(list, "Book ratings fetched"));
    }

    @GetMapping("/book/{bookId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long bookId) {
        double avg = bookRatingService.getAverageRating(bookId);
        return ResponseEntity.ok(ApiResponse.success(avg, "Book average rating fetched"));
    }
}
