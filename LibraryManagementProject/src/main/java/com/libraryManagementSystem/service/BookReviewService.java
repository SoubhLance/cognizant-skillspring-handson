package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.BookReviewDto;
import com.libraryManagementSystem.dto.BookReviewRequest;

import java.util.List;

public interface BookReviewService {
    BookReviewDto addReview(BookReviewRequest request);
    List<BookReviewDto> getBookReviews(Long bookId);
    void deleteReview(Long id);
}
