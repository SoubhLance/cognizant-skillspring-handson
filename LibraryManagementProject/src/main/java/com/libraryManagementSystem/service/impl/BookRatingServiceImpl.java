package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.BookRatingDto;
import com.libraryManagementSystem.dto.BookRatingRequest;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.mapper.BookRatingMapper;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.repository.BookRatingRepository;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.service.BookRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookRatingServiceImpl implements BookRatingService {

    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRatingMapper bookRatingMapper;

    @Override
    @Transactional
    public BookRatingDto addRating(BookRatingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookRating rating = bookRatingRepository.findByUserIdAndBookId(user.getId(), book.getId())
                .orElse(null);

        if (rating == null) {
            rating = BookRating.builder()
                    .user(user)
                    .book(book)
                    .ratingValue(request.getRatingValue())
                    .build();
        } else {
            rating.setRatingValue(request.getRatingValue());
        }

        rating = bookRatingRepository.save(rating);
        return bookRatingMapper.toDto(rating);
    }

    @Override
    public List<BookRatingDto> getBookRatings(Long bookId) {
        return bookRatingRepository.findByBookId(bookId).stream()
                .map(bookRatingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageRating(Long bookId) {
        List<BookRating> ratings = bookRatingRepository.findByBookId(bookId);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream()
                .mapToInt(BookRating::getRatingValue)
                .average()
                .orElse(0.0);
    }
}
