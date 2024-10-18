package com.rag.ai.core;

import com.rag.ai.core.model.Response;
import com.rag.ai.model.ExecuteRequest;

public interface IOpenApiExecutor {
    Response execute(ExecuteRequest executeRequest);
}
