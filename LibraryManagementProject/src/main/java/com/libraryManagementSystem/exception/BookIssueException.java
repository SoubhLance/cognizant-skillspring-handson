package com.libraryManagementSystem.exception;

/**
 * Thrown when a business rule violation occurs during a book issue / return / renew operation.
 * Maps to HTTP 400 Bad Request in GlobalExceptionHandler.
 */
public class BookIssueException extends RuntimeException {
    public BookIssueException(String message) {
        super(message);
    }
}
