package com.rag.ai.core.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rag.ai.core.IOpenApiParser;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class YamlOpenApiParser implements IOpenApiParser {

    private final ObjectMapper objectMapper;
    private final ObjectMapper yamlObjectMapper;

    @Autowired
    public YamlOpenApiParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.yamlObjectMapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    public OpenAPI parse(File filePath) throws IOException {
        final JsonNode jsonNode = yamlObjectMapper.readTree(filePath);
        return objectMapper.readValue(objectMapper.writeValueAsString(jsonNode), OpenAPI.class);
    }
}