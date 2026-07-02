package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.WishlistDto;
import com.libraryManagementSystem.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "coverImageUrl", source = "book.coverImageUrl")
    @Mapping(target = "isbn", source = "book.isbn")
    WishlistDto toDto(Wishlist wishlist);
}
