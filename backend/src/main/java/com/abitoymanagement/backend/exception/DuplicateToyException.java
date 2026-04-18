package com.abitoymanagement.backend.exception;

public class DuplicateToyException extends RuntimeException {

    public DuplicateToyException(String message) {
        super(message);
    }
}
