package com.example.it32007telegram.exceptions;

public class CustomInternalServerError extends RuntimeException {
    public CustomInternalServerError(String msg) {
        super(msg);
    }
}
