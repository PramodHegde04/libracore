package com.pramod.library.service;

import com.pramod.library.entity.Book;
import com.pramod.library.entity.BookIssue;
import com.pramod.library.entity.IssueStatus;
import com.pramod.library.entity.User;
import com.pramod.library.repository.BookIssueRepository;
import com.pramod.library.repository.BookRepository;
import com.pramod.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookIssueService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookIssueRepository bookIssueRepository;

    public BookIssueService(
            BookRepository bookRepository,
            UserRepository userRepository,
            BookIssueRepository bookIssueRepository) {

        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookIssueRepository = bookIssueRepository;
    }

    @Transactional
    public BookIssue issueBook(Long bookId, String email) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with id: " + bookId
                        )
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with email: " + email
                        )
                );

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException(
                    "No available copies for book: " + book.getTitle()
            );
        }

        boolean alreadyIssued =
                bookIssueRepository
                        .findByUserAndBookAndStatus(
                                user,
                                book,
                                IssueStatus.ISSUED
                        )
                        .isPresent();

        if (alreadyIssued) {
            throw new IllegalStateException(
                    "User has already issued this book"
            );
        }

        BookIssue bookIssue = new BookIssue();

        bookIssue.setUser(user);
        bookIssue.setBook(book);
        bookIssue.setIssueDate(LocalDateTime.now());
        bookIssue.setStatus(IssueStatus.ISSUED);

        book.setAvailableCopies(
                book.getAvailableCopies() - 1
        );

        bookRepository.save(book);

        return bookIssueRepository.save(bookIssue);
    }

    @Transactional
    public BookIssue returnBook(Long bookId, String email) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with id: " + bookId
                        )
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with email: " + email
                        )
                );

        BookIssue bookIssue =
                bookIssueRepository
                        .findByUserAndBookAndStatus(
                                user,
                                book,
                                IssueStatus.ISSUED
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No active issue found for this user and book"
                                )
                        );

        bookIssue.setStatus(IssueStatus.RETURNED);
        bookIssue.setReturnDate(LocalDateTime.now());

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new IllegalStateException(
                    "Available copies cannot exceed total copies"
            );
        }

        book.setAvailableCopies(
                book.getAvailableCopies() + 1
        );

        bookRepository.save(book);

        return bookIssueRepository.save(bookIssue);
    }
}