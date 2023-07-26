package com.example.partypal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Server Error")
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

}
