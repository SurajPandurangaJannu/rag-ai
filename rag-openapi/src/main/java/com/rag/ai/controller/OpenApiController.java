package com.rag.ai.controller;

import com.rag.ai.core.model.OpenAPIResponse;
import com.rag.ai.service.OpenApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/openapi")
public class OpenApiController {

    private final OpenApiService openApiService;

    public OpenApiController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    @PostMapping("/upload")
    public OpenAPIResponse uploadOpenApiSpec(@RequestParam("file") MultipartFile file) throws IOException {
        final File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);
        return openApiService.uploadOpenApiSpec(tempFile);
    }

}
