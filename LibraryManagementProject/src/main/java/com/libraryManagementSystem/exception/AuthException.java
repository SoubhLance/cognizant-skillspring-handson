package com.libraryManagementSystem.exception;

/**
 * Thrown when an authentication or account status check fails.
 * Maps to HTTP 401 Unauthorized in GlobalExceptionHandler.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
