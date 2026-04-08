package com.example.CVRUK_backend.common.exception;

public class resourceNotFoundException extends RuntimeException {

    public resourceNotFoundException(String message) {
        super(message);
    }
}
