package com.rag.ai.service;

import com.rag.ai.core.IOpenAPIProvider;
import com.rag.ai.core.model.OpenAPIRequest;
import com.rag.ai.core.model.OpenAPIResponse;
import com.rag.ai.core.model.Response;
import com.rag.ai.model.ExecuteRequest;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class OpenApiService {

    private final IOpenAPIProvider openAPIProvider;

    @Autowired
    public OpenApiService(IOpenAPIProvider openAPIProvider){
        this.openAPIProvider = openAPIProvider;
    }

    public OpenAPIResponse uploadOpenApiSpec(File filePath) throws IOException {
        final OpenAPI openAPI = openAPIProvider.parse(filePath);
        final OpenAPIRequest request = new OpenAPIRequest(openAPI);
        openAPIProvider.load(request);
        return new OpenAPIResponse(openAPI);
    }

    public Response execute(ExecuteRequest executeRequest) throws IOException {
        return openAPIProvider.execute(executeRequest);
    }

}
