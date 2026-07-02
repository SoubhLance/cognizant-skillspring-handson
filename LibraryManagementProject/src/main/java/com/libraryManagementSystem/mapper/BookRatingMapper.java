package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.BookRatingDto;
import com.libraryManagementSystem.entity.BookRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookRatingMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "userId", source = "user.id")
    BookRatingDto toDto(BookRating rating);
}
