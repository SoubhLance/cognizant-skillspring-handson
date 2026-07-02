package com.libraryManagementSystem.mapper;

import com.libraryManagementSystem.dto.CategoryDto;
import com.libraryManagementSystem.dto.CategoryRequest;
import com.libraryManagementSystem.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryRequest request);
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
