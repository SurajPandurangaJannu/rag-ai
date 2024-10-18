package com.rag.ai.core;

import io.swagger.v3.oas.models.OpenAPI;

import java.io.File;
import java.io.IOException;

public interface IOpenApiParser {
    OpenAPI parse(File filePath) throws IOException;
}