package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.BookDto;
import com.libraryManagementSystem.entity.Book;
import com.libraryManagementSystem.entity.Category;
import com.libraryManagementSystem.entity.Publisher;
import com.libraryManagementSystem.mapper.BookMapper;
import com.libraryManagementSystem.repository.BookRepository;
import com.libraryManagementSystem.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private BookDto testBookDto;

    @BeforeEach
    void setUp() {
        Category category = Category.builder().id(1L).name("CS").build();
        Publisher publisher = Publisher.builder().id(1L).name("Prentice Hall").build();
        
        testBook = Book.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("9780132350884")
                .category(category)
                .publisher(publisher)
                .authors(Collections.emptySet())
                .totalCopies(5)
                .availableCopies(5)
                .build();

        testBookDto = BookDto.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("9780132350884")
                .categoryName("CS")
                .publisherName("Prentice Hall")
                .totalCopies(5)
                .availableCopies(5)
                .build();
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);

        BookDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals("9780132350884", result.getIsbn());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getBookById(2L));
        verify(bookRepository, times(1)).findById(2L);
    }
}
