## API execution via text commands using RAG

### Goal
-   Goal of the project is to execute the APIs defined in OpenAPI specification at run time over text command.

### Execution
1. The administrator will load the OpenAPI specification to the application via REST API with additional metadata.
    1. The OpenAPI specification with additional metadata is stored in Vector Database(**PgVector**).
   
2. The user provides a text command to execute the API defined in the OpenAPI specification
    1. The user input text is used to find the nearest embeddings in the vector database.
    2. The documents of vector search is fead to LLMs with additional constraints to get the desired output in JSON format as defined in [here](src/main/java/com/rag/ai/core/model/ExecutionRequestMetaData.java)
    3. The JSON document is used to execute the API with custom logics
    4. The end result(JSON) is presented to end user.

### Useful Links
- https://app.swaggerhub.com/search
- https://goofmint.github.io/cURLtoSwagger