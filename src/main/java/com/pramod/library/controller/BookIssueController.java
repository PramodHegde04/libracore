package com.pramod.library.controller;

import com.pramod.library.dto.BookIssueResponse;
import com.pramod.library.entity.BookIssue;
import com.pramod.library.service.BookIssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookIssueController {

    private final BookIssueService bookIssueService;

    public BookIssueController(BookIssueService bookIssueService) {
        this.bookIssueService = bookIssueService;
    }

    @PostMapping("/{bookId}/issue")
    public ResponseEntity<BookIssueResponse> issueBook(
            @PathVariable Long bookId,
            Authentication authentication) {

        String email = authentication.getName();

        BookIssue bookIssue =
                bookIssueService.issueBook(bookId, email);

        return ResponseEntity.ok(
                createResponse(bookIssue)
        );
    }

    @PostMapping("/{bookId}/return")
    public ResponseEntity<BookIssueResponse> returnBook(
            @PathVariable Long bookId,
            Authentication authentication) {

        String email = authentication.getName();

        BookIssue bookIssue =
                bookIssueService.returnBook(bookId, email);

        return ResponseEntity.ok(
                createResponse(bookIssue)
        );
    }

    private BookIssueResponse createResponse(BookIssue bookIssue) {

        return new BookIssueResponse(
                bookIssue.getId(),
                bookIssue.getBook().getId(),
                bookIssue.getBook().getTitle(),
                bookIssue.getUser().getId(),
                bookIssue.getUser().getName(),
                bookIssue.getUser().getEmail(),
                bookIssue.getIssueDate(),
                bookIssue.getReturnDate(),
                bookIssue.getStatus()
        );
    }
}