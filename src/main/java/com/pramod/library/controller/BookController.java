package com.pramod.library.controller;

import com.pramod.library.dto.BookRequest;
import com.pramod.library.dto.BookResponse;
import com.pramod.library.entity.Book;
import com.pramod.library.mapper.BookMapper;
import com.pramod.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookMapper bookMapper;

    @PostMapping
    public ResponseEntity<BookResponse> saveBook(
            @Valid @RequestBody BookRequest request) {

        Book book = bookMapper.toEntity(request);
        Book savedBook = bookService.saveBook(book);
        BookResponse response = bookMapper.toResponse(savedBook);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1) {
            throw new IllegalArgumentException(
                    "Page size must be at least 1"
            );
        }

        Pageable pageable;

        if (sort != null && !sort.trim().isEmpty()) {

            String[] sortParts = sort.split(",");

            String field = sortParts[0];

            Sort.Direction direction = Sort.Direction.ASC;

            if (sortParts.length > 1) {
                direction = Sort.Direction.fromString(sortParts[1]);
            }

            pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(direction, field)
            );

        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<Book> books;

        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(search.trim(), pageable);
        } else {
            books = bookService.getAllBooks(pageable);
        }

        Page<BookResponse> responses =
                books.map(bookMapper::toResponse);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id) {

        Book book = bookService.getBookById(id);
        BookResponse response = bookMapper.toResponse(book);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {

        Book book = bookMapper.toEntity(request);
        Book updatedBook = bookService.updateBook(id, book);
        BookResponse response = bookMapper.toResponse(updatedBook);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}