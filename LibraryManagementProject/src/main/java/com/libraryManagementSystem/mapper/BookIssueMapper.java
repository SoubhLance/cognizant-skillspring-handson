package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.BookIssueDto;
import com.libraryManagementSystem.entity.BookIssue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookIssueMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userName", expression = "java(issue.getUser().getFirstName() + \" \" + issue.getUser().getLastName())")
    @Mapping(target = "copyId", source = "bookCopy.id")
    @Mapping(target = "copyBarcode", source = "bookCopy.barcode")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    @Mapping(target = "isbn", source = "bookCopy.book.isbn")
    BookIssueDto toDto(BookIssue issue);
}
