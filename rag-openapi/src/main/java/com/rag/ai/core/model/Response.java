package com.rag.ai.core.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public record Response(Object body, HttpStatusCode statusCode, HttpHeaders httpHeaders) {}