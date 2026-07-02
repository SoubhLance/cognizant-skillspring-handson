package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.BookDto;
import com.libraryManagementSystem.dto.BookRequest;
import com.libraryManagementSystem.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class})
public interface BookMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "publisherId", source = "publisher.id")
    @Mapping(target = "publisherName", source = "publisher.name")
    BookDto toDto(Book book);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "authors", ignore = true)
    Book toEntity(BookRequest request);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "authors", ignore = true)
    void updateEntity(BookRequest request, @MappingTarget Book book);
}
