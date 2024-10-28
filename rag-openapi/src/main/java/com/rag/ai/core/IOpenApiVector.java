package com.rag.ai.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rag.ai.core.model.OpenAPIRequest;

public interface IOpenApiVector {

    void load(OpenAPIRequest openAPIRequest) ;

}
