package com.rag.ai.core.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ai.core.IOpenApiVector;
import com.rag.ai.core.model.OpenAPIRequest;
import com.rag.ai.core.model.OpenAPIVectorDocument;
import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.*;

@lombok.extern.slf4j.Slf4j
@Component
@Slf4j
public class OpenApiVectorImpl implements IOpenApiVector {

    private final TokenTextSplitter tokenTextSplitter;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;

    private static final TypeReference<Map<String, Object>> MAP_REFERENCE = new TypeReference<Map<String, Object>>() {
    };

    public OpenApiVectorImpl(VectorStore vectorStore, TokenTextSplitter tokenTextSplitter) {
        this.vectorStore = vectorStore;
        this.objectMapper = new ObjectMapper();
        this.tokenTextSplitter = tokenTextSplitter;

        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void load(OpenAPIRequest openAPIRequest)  {
        final OpenAPI openAPI = openAPIRequest.openAPI();
        final List<String> serverUrls = openAPI.getServers().stream().map(Server::getUrl).toList();
        final List<String> parentTags = openAPI.getTags() != null ? openAPI.getTags().stream().map(Tag::getName).toList() : Collections.emptyList();
        final Paths paths = openAPI.getPaths();
        for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
            final String path = entry.getKey();
            final PathItem pathItem = entry.getValue();

            final var openApiMetaData = new OpenAPIMetaData(serverUrls, parentTags, path);

            final var getOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getGet(), openApiMetaData, HttpMethod.GET.name(), new String[]{"read", "get", "retrieve"});
            final var postOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getPost(), openApiMetaData, HttpMethod.POST.name(), new String[]{"create", "post"});
            final var putOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getPut(), openApiMetaData, HttpMethod.PUT.name(), new String[]{"update", "modify", "put"});
            final var patchOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getPatch(), openApiMetaData, HttpMethod.PATCH.name(), new String[]{"update", "modify", "patch"});
            final var deleteOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getDelete(), openApiMetaData, HttpMethod.DELETE.name(), new String[]{"delete", "remove"});
            final var optionsOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getOptions(), openApiMetaData, HttpMethod.OPTIONS.name(), new String[]{"options"});
            final var headOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getHead(), openApiMetaData, HttpMethod.HEAD.name(), new String[]{"head"});
            final var traceOperationDocument = getOpenAPIVectorDocument(openAPI, pathItem, pathItem.getTrace(), openApiMetaData, HttpMethod.TRACE.name(), new String[]{"trace"});

            loadOpenAPIVectorDocumentIntoVectorStore(getOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(postOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(putOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(patchOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(deleteOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(optionsOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(headOperationDocument);
            loadOpenAPIVectorDocumentIntoVectorStore(traceOperationDocument);
        }
    }

    private void loadOpenAPIVectorDocumentIntoVectorStore(Optional<OpenAPIVectorDocument> apiVectorDocument) {
        if (apiVectorDocument.isPresent()) {
            final var openAPIVectorDocument = apiVectorDocument.get();
            final var content = openAPIVectorDocument.getTitle() + StringUtils.SPACE + openAPIVectorDocument.getDescription() + StringUtils.SPACE + openAPIVectorDocument.getVersion() + StringUtils.SPACE +
                    openAPIVectorDocument.getOperationId() + StringUtils.SPACE + openAPIVectorDocument.getSummary() + StringUtils.SPACE +
                    String.join(",", openAPIVectorDocument.getTags()) + StringUtils.SPACE +
                    String.join(",", openAPIVectorDocument.getHttpMethodAlias()) + StringUtils.SPACE + openAPIVectorDocument.getPath();
            final var values = objectMapper.convertValue(openAPIVectorDocument, MAP_REFERENCE);
            final var document = new Document(content, values);
            final var documents = tokenTextSplitter.apply(List.of(document));
            vectorStore.add(documents);
        }
    }

    private Optional<OpenAPIVectorDocument> getOpenAPIVectorDocument(OpenAPI openAPI, PathItem pathItem, Operation operation, OpenAPIMetaData openAPIMetaData, String httpMethod, String[] httpMethodAlias)  {
        if (operation == null) {
            return Optional.empty();
        }
        Optional<Schema> schemaOptional = Optional.empty();
        final RequestBody requestBody = operation.getRequestBody();
        if (requestBody != null) {
            final Content requestBodyContent = requestBody.getContent();
            final var mediaType = requestBodyContent.get(MediaType.APPLICATION_JSON_VALUE);
            if (mediaType != null) {
                final Schema schema = mediaType.getSchema();
                if (schema.getProperties() != null) {
                    schemaOptional = Optional.of(schema);
                } else {
                    final String ref = schema.get$ref();
                    final String[] split = ref.split("/");
                    final Schema componentSchema = openAPI.getComponents().getSchemas().get(split[split.length - 1]);
                    if (componentSchema.getProperties() != null) {
                        schemaOptional = Optional.of(componentSchema);
                    }
                }
            }
        }

        final List<String> operationTags = operation.getTags() != null ? operation.getTags() : Collections.emptyList();

        final List<String> totalTags = new ArrayList<>(openAPIMetaData.parentsTags());
        totalTags.addAll(operationTags);

        final String operationId = operation.getOperationId();
        final String summary = operation.getSummary() != null ? operation.getSummary() : StringUtils.EMPTY;
        final List<Parameter> parameters = operation.getParameters() != null ? operation.getParameters() : Collections.emptyList();

        final List<VectorParameter> pathParams = parameters.stream().filter(parameter -> parameter.getIn().equals("path"))
                .map(this::convert).toList();
        final List<VectorParameter> headers = parameters.stream().filter(parameter -> parameter.getIn().equals("header"))
                .map(this::convert).toList();
        final List<VectorParameter> queryParams = parameters.stream().filter(parameter -> parameter.getIn().equals("query"))
                .map(this::convert).toList();

        final OpenAPIVectorDocument openAPIVectorDocument = new OpenAPIVectorDocument();
        openAPIVectorDocument.setTitle(openAPI.getInfo().getTitle());
        openAPIVectorDocument.setDescription(openAPI.getInfo().getDescription());
        openAPIVectorDocument.setVersion(openAPI.getInfo().getVersion());
        openAPIVectorDocument.setSummary(summary);
        openAPIVectorDocument.setTags(totalTags);
        openAPIVectorDocument.setUrls(openAPIMetaData.parentsServerUrls());
        openAPIVectorDocument.setPath(openAPIMetaData.path());
        openAPIVectorDocument.setOperationId(operationId);
        openAPIVectorDocument.setHttpMethod(httpMethod);
        openAPIVectorDocument.setHttpMethodAlias(httpMethodAlias);
        openAPIVectorDocument.setHeaders(headers);
        openAPIVectorDocument.setQueryParams(queryParams);
        openAPIVectorDocument.setPathParams(pathParams);
        if (schemaOptional.isPresent()){
            Schema schema = schemaOptional.get();
            try {
                var schemaString = objectMapper.writeValueAsString(schema);
                JsonNode jsonNode = objectMapper.readTree(schemaString);
                openAPIVectorDocument.setSchema(jsonNode);
            }catch (Exception e){
                openAPIVectorDocument.setSchema(schema);
            }
        }
        return Optional.of(openAPIVectorDocument);
    }

    private VectorParameter convert(Parameter parameter) {
        return new VectorParameter(parameter.getName(), parameter.getDescription(), parameter.getRequired() != null && parameter.getRequired());
    }

    public record VectorParameter(String name, String description, boolean required) {
    }

    public record OpenAPIMetaData(List<String> parentsServerUrls, List<String> parentsTags, String path) {
    }
}
