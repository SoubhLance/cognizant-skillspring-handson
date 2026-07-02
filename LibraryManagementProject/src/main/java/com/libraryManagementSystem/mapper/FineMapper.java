package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.FineDto;
import com.libraryManagementSystem.entity.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FineMapper {
    @Mapping(target = "issueId", source = "bookIssue.id")
    @Mapping(target = "bookTitle", source = "bookIssue.bookCopy.book.title")
    @Mapping(target = "userName", expression = "java(fine.getBookIssue().getUser().getFirstName() + \" \" + fine.getBookIssue().getUser().getLastName())")
    FineDto toDto(Fine fine);
}
