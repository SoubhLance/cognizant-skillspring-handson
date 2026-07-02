package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.BookCopy;
import com.libraryManagementSystem.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);
    List<BookCopy> findByBookId(Long bookId);
    long countByBookIdAndStatus(Long bookId, BookCopyStatus status);
}
