package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.CategoryDto;
import com.libraryManagementSystem.dto.CategoryRequest;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long id);
    CategoryDto createCategory(CategoryRequest request);
    CategoryDto updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
