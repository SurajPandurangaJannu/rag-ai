package com.rag.ai.exception;

public class NoMatchFoundException extends RuntimeException {
    public NoMatchFoundException(String message){
        super(message);
    }
}
