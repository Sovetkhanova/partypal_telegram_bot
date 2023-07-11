package com.example.it32007telegram.exceptions;

import com.example.it32007telegram.models.entities.base.BaseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The requested data or resource was not found.")
public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg) {
        super(msg);
    }

    public static NotFoundException notFoundWithId(Class<? extends BaseEntity> clazz, Long id) {
        return notFoundWithProperty(clazz, "id", id.toString());
    }

    public static NotFoundException notFoundWithProperty(Class<? extends BaseEntity> clazz, String property, String value) {
        String message = String.format("%s is not found by %s: %s",
                clazz.getSimpleName(),
                property,
                value);
        return new NotFoundException(message);
    }

}
