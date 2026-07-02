package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.WishlistDto;
import com.libraryManagementSystem.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishlist(@RequestParam Long userId, @RequestParam Long bookId) {
        WishlistDto item = wishlistService.addToWishlist(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(item, "Book added to wishlist"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@RequestParam Long userId, @RequestParam Long bookId) {
        wishlistService.removeFromWishlist(userId, bookId);
        return ResponseEntity.ok(ApiResponse.success("Book removed from wishlist"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<WishlistDto>>> getUserWishlist(@PathVariable Long userId) {
        List<WishlistDto> list = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(ApiResponse.success(list, "User wishlist items fetched"));
    }
}
