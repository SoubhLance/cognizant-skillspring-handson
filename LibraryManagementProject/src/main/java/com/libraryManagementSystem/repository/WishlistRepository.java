package com.libraryManagementSystem.repository;

import com.libraryManagementSystem.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserId(Long userId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    Optional<Wishlist> findByUserIdAndBookId(Long userId, Long bookId);
}
