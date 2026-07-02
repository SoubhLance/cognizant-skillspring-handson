package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRating, Long> {
    List<BookRating> findByBookId(Long bookId);
    Optional<BookRating> findByUserIdAndBookId(Long userId, Long bookId);
}
