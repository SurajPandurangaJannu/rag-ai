package com.rag.ai.controller;

import com.rag.ai.core.model.Response;
import com.rag.ai.model.ExecuteRequest;
import com.rag.ai.service.OpenApiService;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/openapi")
@Slf4j
public class ExecuteController {

    private static final Logger log = LoggerFactory.getLogger(ExecuteController.class);
    private final OpenApiService openApiService;

    public ExecuteController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    @PostMapping("/execute")
    public ResponseEntity<Object> uploadOpenApiSpec(@RequestBody ExecuteRequest executeRequest) throws IOException {
        final Response response = openApiService.execute(executeRequest);
        log.info("Received the response {} {}", response.statusCode(),response.body());
        return new ResponseEntity<>(response.body(),response.httpHeaders(),response.statusCode());
    }

}
