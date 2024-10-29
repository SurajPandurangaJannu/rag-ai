package com.rag.ai.core;

import com.rag.ai.core.model.ExecutionResponse;
import com.rag.ai.model.ExecuteRequest;

public interface IOpenApiExecutor {
    ExecutionResponse execute(ExecuteRequest executeRequest);
}
