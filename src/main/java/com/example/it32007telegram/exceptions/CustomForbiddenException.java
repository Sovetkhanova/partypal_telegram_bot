package com.example.it32007telegram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "The server understood the request, but it refuses to fulfill it due to restrictions in access for the client to the specified resource..")
public class CustomForbiddenException extends RuntimeException {
    public CustomForbiddenException(String msg) {
        super(msg);
    }
}