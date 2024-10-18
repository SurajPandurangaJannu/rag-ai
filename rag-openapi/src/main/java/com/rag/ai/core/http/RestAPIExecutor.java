package com.rag.ai.core.http;

import com.rag.ai.core.model.Request;
import com.rag.ai.core.model.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestAPIExecutor {

    private static final List<String> filterHeaders = List.of("server","date","content-length","via","alt-svc","access-control-allow-origin");
    private final RestTemplate restTemplate;

    public RestAPIExecutor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Response execute(Request template) {
        final HttpHeaders headers = new HttpHeaders();
        if (template.headers() != null) {
            headers.setAll(template.headers());
        }
        final HttpEntity<Object> entity = new HttpEntity<>(template.body(), headers);
        final HttpMethod httpMethod = getHttpMethod(template.httpMethod());
        final String url = generateUrl(template);
        final ResponseEntity<Object> executionResponse = restTemplate.exchange(url, httpMethod, entity, Object.class, template.pathParams());
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

    private String generateUrl(Request template) {
        String path = template.path();
        if (template.pathParams() != null) {
            for (Map.Entry<String, String> entry : template.pathParams().entrySet()) {
                path = path.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

//        String queryParams = template.getQueryParams() != null ?
//                template.getQueryParams().entrySet().stream()
//                        .map(e -> e.getKey() + "=" + e.getValue())
//                        .collect(Collectors.joining("&", "?", ""))
//                : "";

        return "https://apimocha.com/myapi2024" + path;
    }

}
