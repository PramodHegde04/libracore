package com.pramod.library.repository;

import com.pramod.library.entity.BookIssue;
import com.pramod.library.entity.IssueStatus;
import com.pramod.library.entity.User;
import com.pramod.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    Optional<BookIssue> findByUserAndBookAndStatus(
            User user,
            Book book,
            IssueStatus status
    );

}