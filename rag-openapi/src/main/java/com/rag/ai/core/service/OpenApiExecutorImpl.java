package com.rag.ai.core.service;

import com.rag.ai.core.IOpenApiExecutor;
import com.rag.ai.core.http.RestAPIExecutor;
import com.rag.ai.core.model.Request;
import com.rag.ai.core.model.Response;
import com.rag.ai.exception.NoMatchFoundException;
import com.rag.ai.model.ExecuteRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Response execute(ExecuteRequest executeRequest) {
        final SearchRequest request = SearchRequest.query(executeRequest.getMessage()).withTopK(3);
        final List<Document> vectorDocuments = vectorStore.similaritySearch(request);
        final List<String> documentList = vectorDocuments.stream().map(Document::getContent).toList();
        final String documents = documentList.stream().collect(Collectors.joining(System.lineSeparator()));

        final BeanOutputConverter<Request> beanOutputConverter = new BeanOutputConverter<>(Request.class);
        final String format = beanOutputConverter.getFormat();
        final PromptTemplate promptTemplate = new PromptTemplate(searchForApiInOpenapiPrompt);
        final Prompt prompt = promptTemplate.create(
                Map.of(QUESTION,executeRequest.getMessage(),DOCUMENTS,documents, FORMAT,format));
        final String response = chatModel.call(prompt).getResult().getOutput().getContent();
        try {
            final Request template = beanOutputConverter.convert(response);
            return restAPIExecutor.execute(template);
        } catch (Exception e) {
            throw  new NoMatchFoundException(response);
        }
    }
}
