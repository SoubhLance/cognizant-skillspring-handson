package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.Fine;
import com.libraryManagementSystem.enums.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByBookIssueUserId(Long userId);
    List<Fine> findByStatus(FineStatus status);
    long countByBookIssueUserIdAndStatus(Long userId, FineStatus status);
}
