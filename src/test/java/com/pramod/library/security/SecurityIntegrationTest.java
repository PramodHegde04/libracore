package com.pramod.library.security;

import com.pramod.library.entity.Book;
import com.pramod.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BookRepository bookRepository;

    private String adminToken;
    private String memberToken;

    @BeforeEach
    void setUp() {

        adminToken = jwtService.generateToken(
                "admin@test.com",
                "ADMIN"
        );

        memberToken = jwtService.generateToken(
                "member@test.com",
                "MEMBER"
        );
    }

    // ============================================================
    // 1. GET BOOKS WITHOUT AUTHENTICATION
    // ============================================================

    @Test
    void getBooks_withoutAuthentication_shouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // 2. GET BOOK BY ID WITHOUT AUTHENTICATION
    // ============================================================

    @Test
    void getBookById_withoutAuthentication_shouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(
                        get("/books/1")
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // 3. MEMBER CAN GET ALL BOOKS
    // ============================================================

    @Test
    void getBooks_withMemberToken_shouldReturnSuccess()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .header(
                                        "Authorization",
                                        "Bearer " + memberToken
                                )
                )
                .andExpect(status().isOk());
    }

    // ============================================================
    // 4. MEMBER CAN GET A BOOK
    // ============================================================

    @Test
    void getBookById_withMemberToken_shouldReturnSuccess()
            throws Exception {

        Book book = createTestBook();

        mockMvc.perform(
                        get("/books/" + book.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + memberToken
                                )
                )
                .andExpect(status().isOk());
    }

    // ============================================================
    // 5. MEMBER CANNOT CREATE BOOK
    // ============================================================

    @Test
    void createBook_withMemberToken_shouldReturnForbidden()
            throws Exception {

        String requestBody = """
                {
                    "title": "Member Book",
                    "author": "Member Author",
                    "isbn": "SEC-MEMBER-001",
                    "category": "Programming",
                    "totalCopies": 5,
                    "availableCopies": 5
                }
                """;

        mockMvc.perform(
                        post("/books")
                                .header(
                                        "Authorization",
                                        "Bearer " + memberToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // 6. ADMIN CAN CREATE BOOK
    // ============================================================

    @Test
    void createBook_withAdminToken_shouldReturnCreated()
            throws Exception {

        String requestBody = """
                {
                    "title": "Admin Book",
                    "author": "Admin Author",
                    "isbn": "SEC-ADMIN-001",
                    "category": "Programming",
                    "totalCopies": 10,
                    "availableCopies": 10
                }
                """;

        mockMvc.perform(
                        post("/books")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    // ============================================================
    // 7. MEMBER CANNOT UPDATE BOOK
    // ============================================================

    @Test
    void updateBook_withMemberToken_shouldReturnForbidden()
            throws Exception {

        Book book = createTestBook();

        String requestBody = """
                {
                    "title": "Updated Book",
                    "author": "Updated Author",
                    "isbn": "SEC-UPDATE-001",
                    "category": "Programming",
                    "totalCopies": 10,
                    "availableCopies": 10
                }
                """;

        mockMvc.perform(
                        put("/books/" + book.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + memberToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // 8. ADMIN CAN UPDATE BOOK
    // ============================================================

    @Test
    void updateBook_withAdminToken_shouldReturnSuccess()
            throws Exception {

        Book book = createTestBook();

        String requestBody = """
                {
                    "title": "Updated Book",
                    "author": "Updated Author",
                    "isbn": "SEC-UPDATED-001",
                    "category": "Programming",
                    "totalCopies": 15,
                    "availableCopies": 15
                }
                """;

        mockMvc.perform(
                        put("/books/" + book.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }

    // ============================================================
    // 9. MEMBER CANNOT DELETE BOOK
    // ============================================================

    @Test
    void deleteBook_withMemberToken_shouldReturnForbidden()
            throws Exception {

        Book book = createTestBook();

        mockMvc.perform(
                        delete("/books/" + book.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + memberToken
                                )
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // 10. ADMIN CAN DELETE BOOK
    // ============================================================

    @Test
    void deleteBook_withAdminToken_shouldReturnNoContent()
            throws Exception {

        Book book = createTestBook();

        mockMvc.perform(
                        delete("/books/" + book.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isNoContent());
    }

    // ============================================================
    // 11. INVALID JWT
    // ============================================================

    @Test
    void getBooks_withInvalidToken_shouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .header(
                                        "Authorization",
                                        "Bearer invalid-token"
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // 12. UNKNOWN ROLE CANNOT CREATE BOOK
    // ============================================================

    @Test
    void createBook_withUnknownRole_shouldReturnForbidden()
            throws Exception {

        String token = jwtService.generateToken(
                "unknown@test.com",
                "UNKNOWN"
        );

        String requestBody = """
                {
                    "title": "Unknown Role Book",
                    "author": "Unknown Author",
                    "isbn": "SEC-UNKNOWN-001",
                    "category": "Programming",
                    "totalCopies": 5,
                    "availableCopies": 5
                }
                """;

        mockMvc.perform(
                        post("/books")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // HELPER METHOD
    // ============================================================

    private Book createTestBook() {

        Book book = new Book();

        book.setTitle("Security Test Book");
        book.setAuthor("Pramod Hegde");

        book.setIsbn(
                "SEC-" + System.currentTimeMillis()
        );

        book.setCategory("Programming");
        book.setTotalCopies(10);
        book.setAvailableCopies(10);

        return bookRepository.save(book);
    }
}