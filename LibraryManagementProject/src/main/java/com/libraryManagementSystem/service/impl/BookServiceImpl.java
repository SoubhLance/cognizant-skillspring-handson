package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.BookDto;
import com.libraryManagementSystem.dto.BookRequest;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.exception.DuplicateBookException;
import com.libraryManagementSystem.exception.BookNotFoundException;
import com.libraryManagementSystem.mapper.BookMapper;
import com.libraryManagementSystem.repository.*;
import com.libraryManagementSystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookDto createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateBookException("A book with ISBN " + request.getIsbn() + " already exists.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));

        Set<Author> authors = new HashSet<>(authorRepository.findAllById(request.getAuthorIds()));
        if (authors.isEmpty()) {
            throw new RuntimeException("At least one valid author is required");
        }

        Book book = bookMapper.toEntity(request);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setAuthors(authors);
        book.setAvailableCopies(request.getTotalCopies() != null ? request.getTotalCopies() : 0);
        book.setTotalCopies(request.getTotalCopies() != null ? request.getTotalCopies() : 0);

        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookDto updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        // If ISBN changed, verify uniqueness
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateBookException("A book with ISBN " + request.getIsbn() + " already exists.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));

        Set<Author> authors = new HashSet<>(authorRepository.findAllById(request.getAuthorIds()));
        if (authors.isEmpty()) {
            throw new RuntimeException("At least one valid author is required");
        }

        // Adjust available copies based on total copy changes
        int diff = request.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));

        bookMapper.updateEntity(request, book);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setAuthors(authors);

        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "books", key = "#id")
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        return bookMapper.toDto(book);
    }

    @Override
    public Page<BookDto> searchBooks(String title, String isbn, Long categoryId, Long authorId, Long publisherId, Boolean available, Pageable pageable) {
        Specification<Book> spec = Specification.where(BookSpecification.hasTitle(title))
                .and(BookSpecification.hasIsbn(isbn))
                .and(BookSpecification.hasCategoryId(categoryId))
                .and(BookSpecification.hasPublisherId(publisherId))
                .and(BookSpecification.hasAuthorId(authorId));

        if (Boolean.TRUE.equals(available)) {
            spec = spec.and(BookSpecification.isAvailable());
        }

        return bookRepository.findAll(spec, pageable).map(bookMapper::toDto);
    }

    @Override
    public List<BookDto> getRecommendations(Long categoryId) {
        // Simple recommendation: return latest 5 books in the same category
        Pageable limit = PageRequest.of(0, 5);
        return bookRepository.findAll(BookSpecification.hasCategoryId(categoryId), limit)
                .map(bookMapper::toDto)
                .getContent();
    }

    @Override
    public List<String> getSearchSuggestions(String query) {
        // Return matching book titles
        Pageable limit = PageRequest.of(0, 5);
        return bookRepository.findAll(BookSpecification.hasTitle(query), limit)
                .map(Book::getTitle)
                .getContent();
    }

    @Override
    @Transactional
    @CacheEvict(value = "books", key = "#id")
    public BookDto updateCoverImage(Long id, String coverImageUrl) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        book.setCoverImageUrl(coverImageUrl);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }
}
