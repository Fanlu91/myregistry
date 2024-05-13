package com.flhai.myregistry.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ExceptionResponse {
    private HttpStatus status;
    private String message;
}
