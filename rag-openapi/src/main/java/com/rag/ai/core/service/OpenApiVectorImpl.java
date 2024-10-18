package com.rag.ai.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ai.core.IOpenApiVector;
import com.rag.ai.core.model.OpenAPIRequest;
import groovy.util.logging.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OpenApiVectorImpl implements IOpenApiVector {

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;

    public OpenApiVectorImpl(VectorStore vectorStore,ObjectMapper objectMapper) {
        this.vectorStore = vectorStore;
        this.objectMapper = objectMapper;
    }

    @Override
    public void load(OpenAPIRequest openAPIRequest) {
        try {
            final String openAPIRequestString = objectMapper.writeValueAsString(openAPIRequest);
            Document document = new Document(openAPIRequestString);
            vectorStore.add(List.of(document));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
