package com.rag.ai.core.model;

import com.rag.ai.core.service.OpenApiVectorImpl;
import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAPIVectorDocument {
    private String title;
    private String description;
    private String version;
    private String summary;
    private List<String> tags;
    private List<String> urls;
    private String path;
    private String operationId;
    private String httpMethod;
    private String[] httpMethodAlias;
    private List<OpenApiVectorImpl.VectorParameter> headers;
    private List<OpenApiVectorImpl.VectorParameter> queryParams;
    private List<OpenApiVectorImpl.VectorParameter> pathParams;
    private Object schema;
}
