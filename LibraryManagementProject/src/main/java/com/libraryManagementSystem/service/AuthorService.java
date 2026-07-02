package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.AuthorDto;
import com.libraryManagementSystem.dto.AuthorRequest;
import java.util.List;

public interface AuthorService {
    List<AuthorDto> getAllAuthors();
    AuthorDto getAuthorById(Long id);
    AuthorDto createAuthor(AuthorRequest request);
    AuthorDto updateAuthor(Long id, AuthorRequest request);
    void deleteAuthor(Long id);
}
