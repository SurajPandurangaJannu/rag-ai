package com.rag.ai.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class OpenAPIExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(NoMatchFoundException.class)
    public ResponseEntity<Error> handleFirstNameShouldNotBeEmptyException(NoMatchFoundException ex, WebRequest web){
        try {
            final Error response = new Error(objectMapper.readTree(ex.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            final Error response = new Error(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
