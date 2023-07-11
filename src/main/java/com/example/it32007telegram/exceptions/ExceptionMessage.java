package com.example.it32007telegram.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ExceptionMessage {
    private String errorCode;
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
