package com.rag.ai.core.service;

import com.rag.ai.core.IOpenApiExecutor;
import com.rag.ai.core.http.RestAPIExecutor;
import com.rag.ai.core.model.ExecutionRequestMetaData;
import com.rag.ai.core.model.ExecutionResponse;
import com.rag.ai.exception.NoMatchFoundException;
import com.rag.ai.model.ExecuteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OpenApiExecutorImpl implements IOpenApiExecutor {

    public static final String FORMAT = "format";
    public static final String DOCUMENTS = "documents";
    public static final String QUESTION = "question";

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    private final RestAPIExecutor restAPIExecutor;

    @Value("classpath:templates/search-for-api-in-openapi.st")
    private Resource searchForApiInOpenapiPrompt;

    public OpenApiExecutorImpl(VectorStore vectorStore,
                               ChatModel chatModel,
                               RestAPIExecutor restAPIExecutor){
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.restAPIExecutor = restAPIExecutor;
    }

    @Override
    public ExecutionResponse execute(ExecuteRequest executeRequest) {
        final SearchRequest request = SearchRequest.query(executeRequest.getMessage()).withTopK(4);
        final List<Document> vectorDocuments = vectorStore.similaritySearch(request);
        final List<String> documentList = vectorDocuments.stream().map(document -> document.getMetadata().toString()).toList();
        final String documents = documentList.stream().collect(Collectors.joining(System.lineSeparator()));

        final BeanOutputConverter<ExecutionRequestMetaData> beanOutputConverter = new BeanOutputConverter<>(ExecutionRequestMetaData.class);
        final String format = beanOutputConverter.getFormat();
        final PromptTemplate promptTemplate = new PromptTemplate(searchForApiInOpenapiPrompt);
        final Prompt prompt = promptTemplate.create(
                Map.of(QUESTION,executeRequest.getMessage(),DOCUMENTS,documents, FORMAT,format));
        final String response = chatModel.call(prompt).getResult().getOutput().getContent();
        log.info(response);
        try {
            final ExecutionRequestMetaData template = beanOutputConverter.convert(response);
            return restAPIExecutor.execute(template);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw  new NoMatchFoundException(response);
        }
    }
}
