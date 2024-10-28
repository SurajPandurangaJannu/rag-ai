package com.rag.ai.core.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.rag.ai.core.service.OpenApiVectorImpl;

import java.util.List;
import java.util.Map;

public record Request(
        @JsonPropertyDescription("OpenAPI specification title")
        String title,
        @JsonPropertyDescription("OpenAPI specification description")
        String description,
        @JsonPropertyDescription("OpenAPI specification version")
        String version,
        @JsonPropertyDescription("API request or operation summary")
        String summary,
        @JsonPropertyDescription("OpenAPI specification server urls")
        List<String> urls,
        @JsonPropertyDescription("API request or operation path")
        String path,
        @JsonPropertyDescription("API request or operation id")
        String operationId,
        @JsonPropertyDescription("API request or operation http method")
        String httpMethod,
        @JsonPropertyDescription("headers mentioned in the headers with value provided")
        Map<String, String> headers,
        @JsonPropertyDescription("queryParams mentioned in the queryParams with value provided")
        Map<String, String> queryParams,
        @JsonPropertyDescription("pathParams mentioned in the pathParams with value provided")
        Map<String, String> pathParams,
        @JsonPropertyDescription("request payload")
        Object body
) {
}
