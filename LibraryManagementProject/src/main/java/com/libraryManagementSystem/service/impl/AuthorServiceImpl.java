package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.AuthorDto;
import com.libraryManagementSystem.dto.AuthorRequest;
import com.libraryManagementSystem.entity.Author;
import com.libraryManagementSystem.mapper.AuthorMapper;
import com.libraryManagementSystem.repository.AuthorRepository;
import com.libraryManagementSystem.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    @Override
    @Cacheable(value = "authors")
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
        return authorMapper.toDto(author);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public AuthorDto createAuthor(AuthorRequest request) {
        if (authorRepository.existsByName(request.getName())) {
            throw new RuntimeException("Author already exists with name: " + request.getName());
        }
        Author author = authorMapper.toEntity(request);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public AuthorDto updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));

        if (!author.getName().equals(request.getName()) && authorRepository.existsByName(request.getName())) {
            throw new RuntimeException("Author already exists with name: " + request.getName());
        }

        authorMapper.updateEntity(request, author);
        author = authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with ID: " + id);
        }
        authorRepository.deleteById(id);
    }
}
