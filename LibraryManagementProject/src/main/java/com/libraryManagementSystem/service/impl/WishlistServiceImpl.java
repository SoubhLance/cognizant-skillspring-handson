package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.WishlistDto;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.mapper.WishlistMapper;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.repository.WishlistRepository;
import com.libraryManagementSystem.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WishlistMapper wishlistMapper;

    @Override
    @Transactional
    public WishlistDto addToWishlist(Long userId, Long bookId) {
        if (wishlistRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new RuntimeException("Book is already in user wishlist");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .book(book)
                .build();

        wishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toDto(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long bookId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }

    @Override
    public List<WishlistDto> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(wishlistMapper::toDto)
                .collect(Collectors.toList());
    }
}
