package com.pramod.library.service;

import com.pramod.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    Book saveBook(Book book);

    List<Book> getAllBooks();

    Page<Book> getAllBooks(Pageable pageable);

    Page<Book> searchBooks(String search, Pageable pageable);

    Book getBookById(Long id);

    Book updateBook(Long id, Book book);

    void deleteBook(Long id);
}