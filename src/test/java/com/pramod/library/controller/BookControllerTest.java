package com.pramod.library.controller;

import tools.jackson.databind.ObjectMapper;

import com.pramod.library.dto.BookRequest;
import com.pramod.library.dto.BookResponse;
import com.pramod.library.entity.Book;
import com.pramod.library.exception.BookNotFoundException;
import com.pramod.library.mapper.BookMapper;
import com.pramod.library.service.BookService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookMapper bookMapper;


    // ============================================================
    // GET /books/{id} - SUCCESS
    // ============================================================

    @Test
    void getBookById_shouldReturnBook_whenBookExists()
            throws Exception {

        Book book = new Book();

        book.setId(1L);
        book.setTitle("Java Fundamentals");
        book.setAuthor("Pramod");
        book.setIsbn("ISBN001");
        book.setCategory("Programming");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        BookResponse response = new BookResponse(
                1L,
                "Java Fundamentals",
                "Pramod",
                "ISBN001",
                "Programming",
                5,
                5
        );

        when(bookService.getBookById(1L))
                .thenReturn(book);

        when(bookMapper.toResponse(book))
                .thenReturn(response);

        mockMvc.perform(
                        get("/books/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Java Fundamentals"))
                .andExpect(jsonPath("$.author")
                        .value("Pramod"))
                .andExpect(jsonPath("$.isbn")
                        .value("ISBN001"))
                .andExpect(jsonPath("$.category")
                        .value("Programming"))
                .andExpect(jsonPath("$.totalCopies")
                        .value(5))
                .andExpect(jsonPath("$.availableCopies")
                        .value(5));
    }


    // ============================================================
    // GET /books/{id} - NOT FOUND
    // ============================================================

    @Test
    void getBookById_shouldThrowException_whenBookDoesNotExist()
            throws Exception {

        when(bookService.getBookById(999L))
                .thenThrow(
                        new BookNotFoundException(
                                "Book not found with id: 999"
                        )
                );

        mockMvc.perform(
                        get("/books/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Book not found with id: 999"));
    }


    // ============================================================
    // GET /books - PAGINATION
    // ============================================================

    @Test
    void getAllBooks_shouldReturnPaginatedBooks()
            throws Exception {

        Book book = new Book();

        book.setId(1L);
        book.setTitle("Java Fundamentals");
        book.setAuthor("Pramod");
        book.setIsbn("ISBN001");
        book.setCategory("Programming");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        BookResponse response = new BookResponse(
                1L,
                "Java Fundamentals",
                "Pramod",
                "ISBN001",
                "Programming",
                5,
                5
        );

        Page<Book> bookPage =
                new PageImpl<>(
                        List.of(book),
                        PageRequest.of(0, 5),
                        1
                );

        when(bookService.getAllBooks(any(Pageable.class)))
                .thenReturn(bookPage);

        when(bookMapper.toResponse(book))
                .thenReturn(response);

        mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Java Fundamentals"))
                .andExpect(jsonPath("$.content[0].author")
                        .value("Pramod"))
                .andExpect(jsonPath("$.size")
                        .value(5))
                .andExpect(jsonPath("$.number")
                        .value(0));
    }


    // ============================================================
    // GET /books - SEARCH
    // ============================================================

    @Test
    void getAllBooks_shouldSearchBooks()
            throws Exception {

        Book book = new Book();

        book.setId(1L);
        book.setTitle("Java Fundamentals");
        book.setAuthor("Pramod");
        book.setIsbn("ISBN001");
        book.setCategory("Programming");
        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        BookResponse response = new BookResponse(
                1L,
                "Java Fundamentals",
                "Pramod",
                "ISBN001",
                "Programming",
                5,
                5
        );

        Page<Book> bookPage =
                new PageImpl<>(
                        List.of(book),
                        PageRequest.of(0, 20),
                        1
                );

        when(bookService.searchBooks(
                any(String.class),
                any(Pageable.class)
        )).thenReturn(bookPage);

        when(bookMapper.toResponse(book))
                .thenReturn(response);

        mockMvc.perform(
                        get("/books")
                                .param("search", "Java")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title")
                        .value("Java Fundamentals"))
                .andExpect(jsonPath("$.content[0].author")
                        .value("Pramod"));
    }


    // ============================================================
    // GET /books - INVALID PAGE
    // ============================================================

    @Test
    void getAllBooks_shouldReturnBadRequest_whenPageIsNegative()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .param("page", "-1")
                                .param("size", "5")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Page number cannot be negative"));
    }


    // ============================================================
    // GET /books - INVALID SIZE
    // ============================================================

    @Test
    void getAllBooks_shouldReturnBadRequest_whenSizeIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "0")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Page size must be at least 1"));
    }


    // ============================================================
    // POST /books - SUCCESS
    // ============================================================

    @Test
    void saveBook_shouldCreateBook_whenRequestIsValid()
            throws Exception {

        BookRequest request = new BookRequest(
                "Spring Boot",
                "Author B",
                "ISBN002",
                "Programming",
                10,
                10
        );

        Book book = new Book();

        book.setId(2L);
        book.setTitle("Spring Boot");
        book.setAuthor("Author B");
        book.setIsbn("ISBN002");
        book.setCategory("Programming");
        book.setTotalCopies(10);
        book.setAvailableCopies(10);

        BookResponse response = new BookResponse(
                2L,
                "Spring Boot",
                "Author B",
                "ISBN002",
                "Programming",
                10,
                10
        );

        when(bookMapper.toEntity(any(BookRequest.class)))
                .thenReturn(book);

        when(bookService.saveBook(book))
                .thenReturn(book);

        when(bookMapper.toResponse(book))
                .thenReturn(response);

        mockMvc.perform(
                        post("/books")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(2))
                .andExpect(jsonPath("$.title")
                        .value("Spring Boot"))
                .andExpect(jsonPath("$.author")
                        .value("Author B"))
                .andExpect(jsonPath("$.isbn")
                        .value("ISBN002"))
                .andExpect(jsonPath("$.category")
                        .value("Programming"))
                .andExpect(jsonPath("$.totalCopies")
                        .value(10))
                .andExpect(jsonPath("$.availableCopies")
                        .value(10));
    }


    // ============================================================
    // POST /books - VALIDATION FAILURE
    // ============================================================

    @Test
    void saveBook_shouldReturnBadRequest_whenTitleIsBlank()
            throws Exception {

        BookRequest request = new BookRequest(
                "",
                "Author B",
                "ISBN002",
                "Programming",
                10,
                10
        );

        mockMvc.perform(
                        post("/books")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("title: Title cannot be blank"));
    }


    // ============================================================
    // PUT /books/{id} - SUCCESS
    // ============================================================

    @Test
    void updateBook_shouldUpdateBook_whenBookExists()
            throws Exception {

        BookRequest request = new BookRequest(
                "Updated Java",
                "Updated Author",
                "ISBN003",
                "Programming",
                8,
                6
        );

        Book book = new Book();

        book.setId(3L);
        book.setTitle("Updated Java");
        book.setAuthor("Updated Author");
        book.setIsbn("ISBN003");
        book.setCategory("Programming");
        book.setTotalCopies(8);
        book.setAvailableCopies(6);

        BookResponse response = new BookResponse(
                3L,
                "Updated Java",
                "Updated Author",
                "ISBN003",
                "Programming",
                8,
                6
        );

        when(bookMapper.toEntity(any(BookRequest.class)))
                .thenReturn(book);

        when(bookService.updateBook(3L, book))
                .thenReturn(book);

        when(bookMapper.toResponse(book))
                .thenReturn(response);

        mockMvc.perform(
                        put("/books/3")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(3))
                .andExpect(jsonPath("$.title")
                        .value("Updated Java"))
                .andExpect(jsonPath("$.author")
                        .value("Updated Author"))
                .andExpect(jsonPath("$.isbn")
                        .value("ISBN003"))
                .andExpect(jsonPath("$.category")
                        .value("Programming"))
                .andExpect(jsonPath("$.totalCopies")
                        .value(8))
                .andExpect(jsonPath("$.availableCopies")
                        .value(6));
    }


    // ============================================================
    // PUT /books/{id} - VALIDATION FAILURE
    // ============================================================

    @Test
    void updateBook_shouldReturnBadRequest_whenTitleIsBlank()
            throws Exception {

        BookRequest request = new BookRequest(
                "",
                "Updated Author",
                "ISBN003",
                "Programming",
                8,
                6
        );

        mockMvc.perform(
                        put("/books/3")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("title: Title cannot be blank"));
    }


    // ============================================================
    // DELETE /books/{id} - SUCCESS
    // ============================================================

    @Test
    void deleteBook_shouldDeleteBook_whenBookExists()
            throws Exception {

        doNothing()
                .when(bookService)
                .deleteBook(3L);

        mockMvc.perform(
                        delete("/books/3")
                )
                .andExpect(status().isNoContent());
    }


    // ============================================================
    // DELETE /books/{id} - NOT FOUND
    // ============================================================

    @Test
    void deleteBook_shouldReturnNotFound_whenBookDoesNotExist()
            throws Exception {

        doThrow(
                new BookNotFoundException(
                        "Book not found with id: 999"
                )
        )
                .when(bookService)
                .deleteBook(999L);

        mockMvc.perform(
                        delete("/books/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Book not found with id: 999"));
    }
}