package com.pramod.library.service;

import com.pramod.library.entity.Book;
import com.pramod.library.entity.BookIssue;
import com.pramod.library.entity.IssueStatus;
import com.pramod.library.entity.User;
import com.pramod.library.repository.BookIssueRepository;
import com.pramod.library.repository.BookRepository;
import com.pramod.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookIssueServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookIssueRepository bookIssueRepository;

    @InjectMocks
    private BookIssueService bookIssueService;

    private Book book;
    private User user;
    private BookIssue bookIssue;

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

        user = new User();

        user.setId(1L);
        user.setName("Pramod");
        user.setEmail("pramod@example.com");

        bookIssue = new BookIssue();

        bookIssue.setUser(user);
        bookIssue.setBook(book);
        bookIssue.setStatus(IssueStatus.ISSUED);
    }

    @Test
    void issueBook_shouldIssueBook_whenBookAndUserAreValid() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("pramod@example.com"))
                .thenReturn(Optional.of(user));

        when(bookIssueRepository.findByUserAndBookAndStatus(
                user,
                book,
                IssueStatus.ISSUED
        )).thenReturn(Optional.empty());

        when(bookIssueRepository.save(
                org.mockito.ArgumentMatchers.any(BookIssue.class)
        )).thenReturn(bookIssue);

        BookIssue result =
                bookIssueService.issueBook(
                        1L,
                        "pramod@example.com"
                );

        assertNotNull(result);
        assertEquals(IssueStatus.ISSUED, result.getStatus());
        assertEquals(4, book.getAvailableCopies());

        verify(bookRepository).findById(1L);
        verify(userRepository)
                .findByEmail("pramod@example.com");
        verify(bookRepository).save(book);
        verify(bookIssueRepository)
                .save(org.mockito.ArgumentMatchers.any(BookIssue.class));
    }

    @Test
    void issueBook_shouldThrowException_whenBookDoesNotExist() {

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookIssueService.issueBook(
                                999L,
                                "pramod@example.com"
                        )
                );

        assertEquals(
                "Book not found with id: 999",
                exception.getMessage()
        );

        verify(bookRepository).findById(999L);
    }

    @Test
    void issueBook_shouldThrowException_whenUserDoesNotExist() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookIssueService.issueBook(
                                1L,
                                "unknown@example.com"
                        )
                );

        assertEquals(
                "User not found with email: unknown@example.com",
                exception.getMessage()
        );

        verify(bookRepository).findById(1L);
        verify(userRepository)
                .findByEmail("unknown@example.com");
    }

    @Test
    void issueBook_shouldThrowException_whenNoCopiesAreAvailable() {

        book.setAvailableCopies(0);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("pramod@example.com"))
                .thenReturn(Optional.of(user));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bookIssueService.issueBook(
                                1L,
                                "pramod@example.com"
                        )
                );

        assertEquals(
                "No available copies for book: Java Fundamentals",
                exception.getMessage()
        );

        verify(bookRepository).findById(1L);
        verify(userRepository)
                .findByEmail("pramod@example.com");
    }

    @Test
    void issueBook_shouldThrowException_whenUserAlreadyIssuedBook() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("pramod@example.com"))
                .thenReturn(Optional.of(user));

        when(bookIssueRepository.findByUserAndBookAndStatus(
                user,
                book,
                IssueStatus.ISSUED
        )).thenReturn(Optional.of(bookIssue));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bookIssueService.issueBook(
                                1L,
                                "pramod@example.com"
                        )
                );

        assertEquals(
                "User has already issued this book",
                exception.getMessage()
        );

        verify(bookIssueRepository)
                .findByUserAndBookAndStatus(
                        user,
                        book,
                        IssueStatus.ISSUED
                );
    }

    @Test
    void returnBook_shouldReturnBook_whenActiveIssueExists() {

        book.setAvailableCopies(4);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("pramod@example.com"))
                .thenReturn(Optional.of(user));

        when(bookIssueRepository.findByUserAndBookAndStatus(
                user,
                book,
                IssueStatus.ISSUED
        )).thenReturn(Optional.of(bookIssue));

        when(bookIssueRepository.save(bookIssue))
                .thenReturn(bookIssue);

        BookIssue result =
                bookIssueService.returnBook(
                        1L,
                        "pramod@example.com"
                );

        assertNotNull(result);
        assertEquals(IssueStatus.RETURNED, result.getStatus());
        assertNotNull(result.getReturnDate());
        assertEquals(5, book.getAvailableCopies());

        verify(bookRepository).findById(1L);
        verify(userRepository)
                .findByEmail("pramod@example.com");
        verify(bookRepository).save(book);
        verify(bookIssueRepository).save(bookIssue);
    }

    @Test
    void returnBook_shouldThrowException_whenBookDoesNotExist() {

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookIssueService.returnBook(
                                999L,
                                "pramod@example.com"
                        )
                );

        assertEquals(
                "Book not found with id: 999",
                exception.getMessage()
        );

        verify(bookRepository).findById(999L);
    }

    @Test
    void returnBook_shouldThrowException_whenUserDoesNotExist() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookIssueService.returnBook(
                                1L,
                                "unknown@example.com"
                        )
                );

        assertEquals(
                "User not found with email: unknown@example.com",
                exception.getMessage()
        );

        verify(bookRepository).findById(1L);
        verify(userRepository)
                .findByEmail("unknown@example.com");
    }

    @Test
    void returnBook_shouldThrowException_whenNoActiveIssueExists() {

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("pramod@example.com"))
                .thenReturn(Optional.of(user));

        when(bookIssueRepository.findByUserAndBookAndStatus(
                user,
                book,
                IssueStatus.ISSUED
        )).thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bookIssueService.returnBook(
                                1L,
                                "pramod@example.com"
                        )
                );

        assertEquals(
                "No active issue found for this user and book",
                exception.getMessage()
        );

        verify(bookIssueRepository)
                .findByUserAndBookAndStatus(
                        user,
                        book,
                        IssueStatus.ISSUED
                );
    }

    @Test
    void returnBook_shouldThrowException_whenAvailableCopiesReachTotalCopies() {

        book.setTotalCopies(5);
        book.setAvailableCopies(5);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        when(userRepository.findByEmail("pramod@example.com"))
                .thenReturn(Optional.of(user));

        when(bookIssueRepository.findByUserAndBookAndStatus(
                user,
                book,
                IssueStatus.ISSUED
        )).thenReturn(Optional.of(bookIssue));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bookIssueService.returnBook(
                                1L,
                                "pramod@example.com"
                        )
                );

        assertEquals(
                "Available copies cannot exceed total copies",
                exception.getMessage()
        );

        verify(bookIssueRepository)
                .findByUserAndBookAndStatus(
                        user,
                        book,
                        IssueStatus.ISSUED
                );
    }
}