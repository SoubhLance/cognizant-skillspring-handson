package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.WishlistDto;

import java.util.List;

public interface WishlistService {
    WishlistDto addToWishlist(Long userId, Long bookId);
    void removeFromWishlist(Long userId, Long bookId);
    List<WishlistDto> getUserWishlist(Long userId);
}
