package com.pramod.library.integration;

import com.pramod.library.dto.BookRequest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // ============================================================
    // 1. CREATE BOOK
    // ============================================================

    @Test
    void createBook_shouldCreateBookSuccessfully() throws Exception {

        String requestBody = """
                {
                    "title": "Integration Testing with Spring Boot",
                    "author": "Pramod Hegde",
                    "isbn": "INT-TEST-001",
                    "category": "Programming",
                    "totalCopies": 10,
                    "availableCopies": 10
                }
                """;

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title")
                        .value("Integration Testing with Spring Boot"))
                .andExpect(jsonPath("$.author")
                        .value("Pramod Hegde"))
                .andExpect(jsonPath("$.isbn")
                        .value("INT-TEST-001"))
                .andExpect(jsonPath("$.category")
                        .value("Programming"))
                .andExpect(jsonPath("$.totalCopies")
                        .value(10))
                .andExpect(jsonPath("$.availableCopies")
                        .value(10));
    }


    // ============================================================
    // 2. GET BOOK BY ID
    // ============================================================

    @Test
    void getBookById_shouldReturnCreatedBook() throws Exception {

        String requestBody = """
                {
                    "title": "Java Backend Development",
                    "author": "Pramod Hegde",
                    "isbn": "INT-TEST-002",
                    "category": "Java",
                    "totalCopies": 5,
                    "availableCopies": 5
                }
                """;

        String response = mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        Long bookId = jsonNode.get("id").asLong();

        mockMvc.perform(
                        get("/books/{id}", bookId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title")
                        .value("Java Backend Development"))
                .andExpect(jsonPath("$.author")
                        .value("Pramod Hegde"))
                .andExpect(jsonPath("$.isbn")
                        .value("INT-TEST-002"));
    }


    // ============================================================
    // 3. GET NON-EXISTING BOOK
    // ============================================================

    @Test
    void getBookById_shouldReturnNotFoundForInvalidId()
            throws Exception {

        mockMvc.perform(
                        get("/books/{id}", 999999L)
                )
                .andExpect(status().isNotFound());
    }


    // ============================================================
    // 4. GET ALL BOOKS
    // ============================================================

    @Test
    void getAllBooks_shouldReturnPaginatedBooks()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));

    }


    // ============================================================
    // 5. SEARCH BOOKS
    // ============================================================

    @Test
    void searchBooks_shouldReturnMatchingBooks()
            throws Exception {

        String requestBody = """
                {
                    "title": "Spring Boot Integration Search",
                    "author": "Pramod Hegde",
                    "isbn": "INT-TEST-003",
                    "category": "Programming",
                    "totalCopies": 8,
                    "availableCopies": 8
                }
                """;

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/books")
                                .param(
                                        "search",
                                        "Spring Boot Integration Search"
                                )
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title")
                        .value("Spring Boot Integration Search"));
    }


    // ============================================================
    // 6. UPDATE BOOK
    // ============================================================

    @Test
    void updateBook_shouldUpdateExistingBook()
            throws Exception {

        String createRequest = """
                {
                    "title": "Original Book Title",
                    "author": "Original Author",
                    "isbn": "INT-TEST-004",
                    "category": "Programming",
                    "totalCopies": 10,
                    "availableCopies": 10
                }
                """;

        String response = mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookId =
                objectMapper.readTree(response)
                        .get("id")
                        .asLong();

        String updateRequest = """
                {
                    "title": "Updated Book Title",
                    "author": "Updated Author",
                    "isbn": "INT-TEST-004",
                    "category": "Java",
                    "totalCopies": 15,
                    "availableCopies": 15
                }
                """;

        mockMvc.perform(
                        put("/books/{id}", bookId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(bookId))
                .andExpect(jsonPath("$.title")
                        .value("Updated Book Title"))
                .andExpect(jsonPath("$.author")
                        .value("Updated Author"))
                .andExpect(jsonPath("$.category")
                        .value("Java"))
                .andExpect(jsonPath("$.totalCopies")
                        .value(15))
                .andExpect(jsonPath("$.availableCopies")
                        .value(15));
    }


    // ============================================================
    // 7. DELETE BOOK
    // ============================================================

    @Test
    void deleteBook_shouldDeleteExistingBook()
            throws Exception {

        String requestBody = """
                {
                    "title": "Book To Delete",
                    "author": "Pramod Hegde",
                    "isbn": "INT-TEST-005",
                    "category": "Testing",
                    "totalCopies": 3,
                    "availableCopies": 3
                }
                """;

        String response = mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookId =
                objectMapper.readTree(response)
                        .get("id")
                        .asLong();

        mockMvc.perform(
                        delete("/books/{id}", bookId)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/books/{id}", bookId)
                )
                .andExpect(status().isNotFound());
    }


    // ============================================================
    // 8. CREATE BOOK - VALIDATION
    // ============================================================

    @Test
    void createBook_shouldReturnBadRequestForInvalidData()
            throws Exception {

        String invalidRequest = """
                {
                    "title": "",
                    "author": "",
                    "isbn": "",
                    "category": "",
                    "totalCopies": 0,
                    "availableCopies": -1
                }
                """;

        mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // 9. GET BOOKS - INVALID PAGE
    // ============================================================

    @Test
    void getAllBooks_shouldReturnBadRequestForNegativePage()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .param("page", "-1")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // 10. GET BOOKS - INVALID SIZE
    // ============================================================

    @Test
    void getAllBooks_shouldReturnBadRequestForInvalidSize()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "0")
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // 11. UPDATE BOOK - VALIDATION
    // ============================================================

    @Test
    void updateBook_shouldReturnBadRequestForInvalidData()
            throws Exception {

        String createRequest = """
                {
                    "title": "Book For Validation",
                    "author": "Pramod Hegde",
                    "isbn": "INT-TEST-006",
                    "category": "Testing",
                    "totalCopies": 5,
                    "availableCopies": 5
                }
                """;

        String response = mockMvc.perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createRequest)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookId =
                objectMapper.readTree(response)
                        .get("id")
                        .asLong();

        String invalidUpdateRequest = """
                {
                    "title": "",
                    "author": "",
                    "isbn": "",
                    "category": "",
                    "totalCopies": 0,
                    "availableCopies": -1
                }
                """;

        mockMvc.perform(
                        put("/books/{id}", bookId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidUpdateRequest)
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // 12. SORT BOOKS
    // ============================================================

    @Test
    void getAllBooks_shouldSupportSorting()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "title,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}