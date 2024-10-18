package com.rag.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class OpenAPIExceptionHandler {

    @ExceptionHandler(NoMatchFoundException.class)
    public ResponseEntity<Error> handleFirstNameShouldNotBeEmptyException(NoMatchFoundException ex, WebRequest web){
        final Error response = new Error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
