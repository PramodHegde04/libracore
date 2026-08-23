package com.pramod.library.service.impl;

import com.pramod.library.entity.Book;
import com.pramod.library.exception.BookNotFoundException;
import com.pramod.library.repository.BookRepository;
import com.pramod.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book saveBook(Book book) {

        validateInventory(book);

        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooks(String search, Pageable pageable) {
        return bookRepository.searchBooks(search, pageable);
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with id: " + id
                        ));
    }

    @Override
    public Book updateBook(Long id, Book book) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with id: " + id
                        ));

        validateInventory(book);

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setCategory(book.getCategory());
        existingBook.setTotalCopies(book.getTotalCopies());
        existingBook.setAvailableCopies(book.getAvailableCopies());

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with id: " + id
                        ));

        bookRepository.delete(existingBook);
    }

    private void validateInventory(Book book) {

        if (book.getTotalCopies() == null ||
                book.getAvailableCopies() == null) {
            throw new IllegalArgumentException(
                    "Total copies and available copies are required"
            );
        }

        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new IllegalArgumentException(
                    "Available copies cannot exceed total copies"
            );
        }
    }
}