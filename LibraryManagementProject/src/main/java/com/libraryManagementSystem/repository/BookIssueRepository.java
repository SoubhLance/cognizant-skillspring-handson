package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.BookIssue;
import com.libraryManagementSystem.enums.BookIssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByUserId(Long userId);
    List<BookIssue> findByStatus(BookIssueStatus status);
    long countByUserIdAndStatus(Long userId, BookIssueStatus status);
    Optional<BookIssue> findFirstByBookCopyBarcodeAndStatusInOrderByIdDesc(String barcode, Collection<BookIssueStatus> statuses);
}
