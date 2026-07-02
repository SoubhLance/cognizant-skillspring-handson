package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.BookRatingDto;
import com.libraryManagementSystem.dto.BookRatingRequest;

import java.util.List;

public interface BookRatingService {
    BookRatingDto addRating(BookRatingRequest request);
    List<BookRatingDto> getBookRatings(Long bookId);
    double getAverageRating(Long bookId);
}
