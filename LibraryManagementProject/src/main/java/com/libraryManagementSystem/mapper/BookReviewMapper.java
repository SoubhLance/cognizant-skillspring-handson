package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.BookReviewDto;
import com.libraryManagementSystem.entity.BookReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookReviewMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(review.getUser().getFirstName() + \" \" + review.getUser().getLastName())")
    BookReviewDto toDto(BookReview review);
}
