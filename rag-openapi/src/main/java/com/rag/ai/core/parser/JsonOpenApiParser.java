package com.rag.ai.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ai.core.IOpenApiParser;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class JsonOpenApiParser implements IOpenApiParser {

    private final ObjectMapper objectMapper;

    @Autowired
    public JsonOpenApiParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public OpenAPI parse(File filePath) throws IOException {
        return objectMapper.readValue(filePath, OpenAPI.class);
    }
}