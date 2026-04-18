package com.abitoymanagement.backend.exception;

public class ToyNotFoundException extends RuntimeException {

    public ToyNotFoundException(String message) {
        super(message);
    }
}
