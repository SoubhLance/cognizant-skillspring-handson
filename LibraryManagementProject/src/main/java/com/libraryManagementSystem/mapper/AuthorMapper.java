package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.AuthorDto;
import com.libraryManagementSystem.dto.AuthorRequest;
import com.libraryManagementSystem.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDto toDto(Author author);
    Author toEntity(AuthorRequest request);
    void updateEntity(AuthorRequest request, @MappingTarget Author author);
}
