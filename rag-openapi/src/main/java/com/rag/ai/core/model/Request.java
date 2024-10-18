package com.rag.ai.core.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.Map;

public record Request(
        @JsonPropertyDescription("servers url")
        String url,
        @JsonPropertyDescription("path or endpoint that should be triggered")
        String path,
        @JsonPropertyDescription("http request method")
        String httpMethod,
        @JsonPropertyDescription("headers mentioned in the parameters with value provided")
        Map<String, String> headers,
        @JsonPropertyDescription("headers mentioned in the parameters with value provided")
        Map<String, String> queryParams,
        @JsonPropertyDescription("path mentioned in the parameters with value provided")
        Map<String, String> pathParams,
        @JsonPropertyDescription("request payload")
        Object body
) {
}
