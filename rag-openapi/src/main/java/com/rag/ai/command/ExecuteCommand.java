package com.rag.ai.command;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.ai.core.model.Response;
import com.rag.ai.model.ExecuteRequest;
import com.rag.ai.service.OpenApiService;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Command
@Slf4j
public class ExecuteCommand {

    private static final Logger log = LoggerFactory.getLogger(ExecuteCommand.class);
    private final OpenApiService openApiService;
    private final ObjectMapper objectMapper;

    public ExecuteCommand(OpenApiService openApiService,ObjectMapper objectMapper) {
        this.openApiService = openApiService;
        this.objectMapper = objectMapper;
        this.objectMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
    }

    @Command(command = "execute")
    public String executeRequest(@ShellOption String message) throws IOException {
        final Response response = openApiService.execute(new ExecuteRequest(message));
        log.info("Received the response {} {}", response.statusCode(),response.body());
        return objectMapper.writeValueAsString(response);
    }

}
