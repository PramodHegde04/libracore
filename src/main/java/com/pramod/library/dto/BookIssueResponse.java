package com.pramod.library.dto;

import com.pramod.library.entity.IssueStatus;

import java.time.LocalDateTime;

public class BookIssueResponse {

    private Long issueId;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;
    private IssueStatus status;

    public BookIssueResponse() {
    }

    public BookIssueResponse(
            Long issueId,
            Long bookId,
            String bookTitle,
            Long userId,
            String userName,
            String userEmail,
            LocalDateTime issueDate,
            LocalDateTime returnDate,
            IssueStatus status) {

        this.issueId = issueId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
}