package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.BookCopyDto;
import com.libraryManagementSystem.entity.BookCopy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    BookCopyDto toDto(BookCopy copy);
}
