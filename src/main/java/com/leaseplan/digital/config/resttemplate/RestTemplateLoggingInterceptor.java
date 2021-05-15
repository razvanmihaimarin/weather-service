package com.leaseplan.digital.config.resttemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Slf4j
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request, @NonNull byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        logRequestDetails(request);
        return execution.execute(request, body);
    }

    private void logRequestDetails(HttpRequest request) {
        log.debug("Request URI: {}", request.getURI());
        log.debug("Method: {}", request.getMethod());
        log.debug("Headers: {}", request.getHeaders());
    }
}
