package com.example.it32007telegram.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class CustomDataIntegrityValidationException extends DataIntegrityViolationException {
    public CustomDataIntegrityValidationException(String msg) {
        super(msg);
    }
}
