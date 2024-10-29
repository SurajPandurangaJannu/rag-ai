package com.rag.ai.core.http;

import com.rag.ai.core.model.Request;
import com.rag.ai.core.model.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RestAPIExecutor {

    private static final List<String> filterHeaders = List.of("server","date","content-length","via","alt-svc","access-control-allow-origin","transfer-encoding","connection");
    private final RestTemplate restTemplate;

    public RestAPIExecutor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Response execute(Request request) {
        final HttpHeaders headers = new HttpHeaders();
        if (request.headers() != null) {
            headers.setAll(request.headers());
        }
        final HttpEntity<Object> entity = new HttpEntity<>(request.body(), headers);
        final HttpMethod httpMethod = getHttpMethod(request.httpMethod());
        final String url = generateUrl(request);
        final ResponseEntity<Object> executionResponse = restTemplate.exchange(url, httpMethod, entity, Object.class, request.pathParams());
        return new Response(executionResponse.getBody(),executionResponse.getStatusCode(), getFilteredHeaders(executionResponse));
    }

    private static HttpHeaders getFilteredHeaders(ResponseEntity<Object> executionResponse) {
        final HttpHeaders headers = new HttpHeaders();
        executionResponse.getHeaders().forEach((k,v) -> {
            if (!filterHeaders.contains(k.toLowerCase()) && !v.isEmpty()){
                headers.add(k,v.get(0));
            }
        });
        return headers;
    }

    private HttpMethod getHttpMethod(String httpMethod) {
        return HttpMethod.valueOf(httpMethod.toUpperCase());
    }

    private String generateUrl(Request request) {
        String path = request.path();
        if (request.pathParams() != null) {
            for (Map.Entry<String, String> entry : request.pathParams().entrySet()) {
                path = path.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        final String queryParams = request.queryParams() != null ?
                request.queryParams().entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&", "?", ""))
                : "";

        return request.urls().get(0) + path + queryParams;
    }

}
