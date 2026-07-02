package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.BookReviewDto;
import com.libraryManagementSystem.dto.BookReviewRequest;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.mapper.BookReviewMapper;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.repository.BookReviewRepository;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.service.BookReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookReviewServiceImpl implements BookReviewService {

    @Autowired
    private BookReviewRepository bookReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookReviewMapper bookReviewMapper;

    @Override
    @Transactional
    public BookReviewDto addReview(BookReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookReview review = BookReview.builder()
                .user(user)
                .book(book)
                .reviewText(request.getReviewText())
                .build();

        review = bookReviewRepository.save(review);
        return bookReviewMapper.toDto(review);
    }

    @Override
    public List<BookReviewDto> getBookReviews(Long bookId) {
        return bookReviewRepository.findByBookId(bookId).stream()
                .map(bookReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!bookReviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found");
        }
        bookReviewRepository.deleteById(id);
    }
}
