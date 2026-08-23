package com.pramod.library.integration;

import com.pramod.library.entity.User;
import com.pramod.library.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ============================================================
    // SETUP
    // ============================================================

    @BeforeEach
    void setUp() {

        /*
         * book_issues.user_id references users.id.
         *
         * Therefore, we must delete child records first
         * and parent records second.
         */
        jdbcTemplate.update("DELETE FROM book_issues");

        userRepository.deleteAll();
    }

    // ============================================================
    // 1. REGISTER SUCCESSFULLY
    // ============================================================

    @Test
    void register_shouldCreateUserAndReturnToken()
            throws Exception {

        String email = uniqueEmail();

        String requestBody = """
                {
                    "name": "Pramod Hegde",
                    "email": "%s",
                    "password": "Password@123"
                }
                """.formatted(email);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.name").value("Pramod Hegde"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    // ============================================================
    // 2. DUPLICATE EMAIL
    // ============================================================

    @Test
    void register_withExistingEmail_shouldReturnBadRequest()
            throws Exception {

        String email = uniqueEmail();

        User user = new User(
                "Existing User",
                email,
                "encoded-password",
                com.pramod.library.entity.Role.MEMBER
        );

        userRepository.save(user);

        String requestBody = """
                {
                    "name": "New User",
                    "email": "%s",
                    "password": "Password@123"
                }
                """.formatted(email);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // 3. REGISTER WITH INVALID DATA
    // ============================================================

    @Test
    void register_withInvalidData_shouldReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                    "name": "",
                    "email": "",
                    "password": ""
                }
                """;

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // 4. LOGIN SUCCESSFULLY
    // ============================================================

    @Test
    void login_withValidCredentials_shouldReturnToken()
            throws Exception {

        String email = uniqueEmail();
        String password = "Password@123";

        registerUser(email, password);

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    // ============================================================
    // 5. LOGIN WITH WRONG PASSWORD
    // ============================================================

    @Test
    void login_withWrongPassword_shouldReturnUnauthorized()
            throws Exception {

        String email = uniqueEmail();

        registerUser(
                email,
                "CorrectPassword@123"
        );

        String requestBody = """
                {
                    "email": "%s",
                    "password": "WrongPassword@123"
                }
                """.formatted(email);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // 6. LOGIN WITH UNKNOWN EMAIL
    // ============================================================

    @Test
    void login_withUnknownEmail_shouldReturnUnauthorized()
            throws Exception {

        String requestBody = """
                {
                    "email": "doesnotexist@example.com",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // 7. LOGIN WITH INVALID DATA
    // ============================================================

    @Test
    void login_withInvalidData_shouldReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                    "email": "",
                    "password": ""
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // 8. REGISTERED USER CAN ACCESS PROTECTED API
    // ============================================================

    @Test
    void registeredUser_shouldAccessBooksUsingJwt()
            throws Exception {

        String email = uniqueEmail();
        String password = "Password@123";

        String token = registerAndGetToken(
                email,
                password
        );

        mockMvc.perform(
                        get("/books")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    // ============================================================
    // 9. REGISTERED MEMBER CANNOT CREATE BOOK
    // ============================================================

    @Test
    void registeredMember_shouldNotCreateBook()
            throws Exception {

        String email = uniqueEmail();
        String password = "Password@123";

        String token = registerAndGetToken(
                email,
                password
        );

        String bookRequest = """
                {
                    "title": "Security Book",
                    "author": "Pramod Hegde",
                    "isbn": "AUTH-SEC-001",
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
                                .content(bookRequest)
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // 10. REGISTER ENDPOINT DOES NOT REQUIRE JWT
    // ============================================================

    @Test
    void register_withoutAuthentication_shouldBeAllowed()
            throws Exception {

        String email = uniqueEmail();

        String requestBody = """
                {
                    "name": "Public Registration",
                    "email": "%s",
                    "password": "Password@123"
                }
                """.formatted(email);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    // ============================================================
    // 11. LOGIN ENDPOINT DOES NOT REQUIRE JWT
    // ============================================================

    @Test
    void login_withoutAuthentication_shouldBeAllowed()
            throws Exception {

        String email = uniqueEmail();
        String password = "Password@123";

        registerUser(email, password);

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }

    // ============================================================
    // 12. INVALID JWT CANNOT ACCESS PROTECTED API
    // ============================================================

    @Test
    void protectedEndpoint_withInvalidJwt_shouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(
                        get("/books")
                                .header(
                                        "Authorization",
                                        "Bearer invalid.jwt.token"
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // HELPER: REGISTER USER
    // ============================================================

    private void registerUser(
            String email,
            String password) throws Exception {

        String requestBody = """
                {
                    "name": "Test User",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    // ============================================================
    // HELPER: REGISTER USER AND EXTRACT JWT
    // ============================================================

    private String registerAndGetToken(
            String email,
            String password) throws Exception {

        String requestBody = """
                {
                    "name": "Test User",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String response = result
                .getResponse()
                .getContentAsString();

        return extractToken(response);
    }

    // ============================================================
    // HELPER: EXTRACT TOKEN
    // ============================================================

    private String extractToken(String response) {

        String key = "\"token\":\"";

        int start = response.indexOf(key);

        if (start == -1) {
            throw new IllegalStateException(
                    "JWT token not found in response: " + response
            );
        }

        start += key.length();

        int end = response.indexOf(
                "\"",
                start
        );

        if (end == -1) {
            throw new IllegalStateException(
                    "Invalid token response: " + response
            );
        }

        return response.substring(
                start,
                end
        );
    }

    // ============================================================
    // HELPER: UNIQUE EMAIL
    // ============================================================

    private String uniqueEmail() {

        return "test-" +
                System.nanoTime() +
                "@example.com";
    }
}