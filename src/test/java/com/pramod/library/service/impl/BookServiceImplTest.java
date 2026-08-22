package com.pramod.library.service.impl;

import com.pramod.library.entity.Book;
import com.pramod.library.exception.BookNotFoundException;
import com.pramod.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();

        book.setId(1L);
        book.setTitle("Java Fundamentals");
        book.setAuthor("Author A");
        book.setIsbn("ISBN001");
        book.setCategory("Programming");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
    }

    @Test
    void saveBook_shouldSaveAndReturnBook() {

        when(bookRepository.save(book))
                .thenReturn(book);

        Book result = bookService.saveBook(book);

        assertEquals(book, result);

        verify(bookRepository).save(book);
    }

    @Test
    void getBookById_shouldReturnBook_whenBookExists() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Java Fundamentals", result.getTitle());
        assertEquals("Author A", result.getAuthor());

        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_shouldThrowException_whenBookDoesNotExist() {

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> bookService.getBookById(999L)
        );

        assertEquals(
                "Book not found with id: 999",
                exception.getMessage()
        );

        verify(bookRepository).findById(999L);
    }

    @Test
    void updateBook_shouldUpdateBook_whenBookExists() {

        Book updatedBook = new Book();

        updatedBook.setTitle("Advanced Java");
        updatedBook.setAuthor("Author B");
        updatedBook.setIsbn("ISBN002");
        updatedBook.setCategory("Programming");
        updatedBook.setTotalCopies(10);
        updatedBook.setAvailableCopies(8);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(bookRepository.save(book))
                .thenReturn(book);

        Book result = bookService.updateBook(1L, updatedBook);

        assertEquals("Advanced Java", result.getTitle());
        assertEquals("Author B", result.getAuthor());
        assertEquals("ISBN002", result.getIsbn());
        assertEquals("Programming", result.getCategory());
        assertEquals(10, result.getTotalCopies());
        assertEquals(8, result.getAvailableCopies());

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_shouldThrowException_whenBookDoesNotExist() {

        Book updatedBook = new Book();

        updatedBook.setTitle("Advanced Java");
        updatedBook.setAuthor("Author B");
        updatedBook.setIsbn("ISBN002");
        updatedBook.setCategory("Programming");
        updatedBook.setTotalCopies(10);
        updatedBook.setAvailableCopies(8);

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> bookService.updateBook(999L, updatedBook)
        );

        assertEquals(
                "Book not found with id: 999",
                exception.getMessage()
        );

        verify(bookRepository).findById(999L);
    }

    @Test
    void deleteBook_shouldDeleteBook_whenBookExists() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_shouldThrowException_whenBookDoesNotExist() {

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookNotFoundException.class,
                () -> bookService.deleteBook(999L)
        );

        verify(bookRepository).findById(999L);
    }
}