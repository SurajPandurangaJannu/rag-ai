package com.rag.ai.core.service;

import com.rag.ai.core.IOpenAPIProvider;
import com.rag.ai.core.IOpenApiExecutor;
import com.rag.ai.core.IOpenApiVector;
import com.rag.ai.core.model.OpenAPIRequest;
import com.rag.ai.core.model.ExecutionResponse;
import com.rag.ai.model.ExecuteRequest;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class OpenApiProviderImpl implements IOpenAPIProvider {

    private final OpenApiParserImpl openApiParser;
    private final IOpenApiVector openApiVector;
    private final IOpenApiExecutor openApiExecutor;

    public OpenApiProviderImpl(OpenApiParserImpl openApiParserFactory,
                               IOpenApiVector openApiVector,
                               IOpenApiExecutor openApiExecutor){
        this.openApiParser = openApiParserFactory;
        this.openApiVector = openApiVector;
        this.openApiExecutor = openApiExecutor;
    }

    @Override
    public OpenAPI parse(File filePath) throws IOException {
        return openApiParser.parse(filePath);
    }

    @Override
    public void load(OpenAPIRequest openAPIRequest)  {
        openApiVector.load(openAPIRequest);
    }

    @Override
    public ExecutionResponse execute(ExecuteRequest executeRequest) {
        return openApiExecutor.execute(executeRequest);
    }
}
