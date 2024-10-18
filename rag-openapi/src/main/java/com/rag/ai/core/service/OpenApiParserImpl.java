package com.rag.ai.core.service;

import com.rag.ai.core.IOpenApiParser;
import com.rag.ai.core.parser.JsonOpenApiParser;
import com.rag.ai.core.parser.YamlOpenApiParser;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class OpenApiParserImpl {

    private final YamlOpenApiParser yamlOpenApiParser;
    private final JsonOpenApiParser jsonOpenApiParser;

    public OpenApiParserImpl(JsonOpenApiParser jsonOpenApiParser, YamlOpenApiParser yamlOpenApiParser){
        this.jsonOpenApiParser = jsonOpenApiParser;
        this.yamlOpenApiParser = yamlOpenApiParser;
    }

    public IOpenApiParser getParser(String fileExtension) {
        return switch (fileExtension.toLowerCase()) {
            case "yaml", "yml" -> yamlOpenApiParser;
            case "json" -> jsonOpenApiParser;
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        };
    }

    public OpenAPI parse(File filePath) throws IOException {
        final String fileExtension = getFileExtension(filePath);
        final IOpenApiParser openApiParser = getParser(fileExtension);
        return openApiParser.parse(filePath);
    }

    private String getFileExtension(File fileName) {
        int lastIndexOfDot = fileName.getAbsolutePath().lastIndexOf(".");
        return (lastIndexOfDot == -1) ? "" : fileName.getAbsolutePath().substring(lastIndexOfDot + 1);
    }

}