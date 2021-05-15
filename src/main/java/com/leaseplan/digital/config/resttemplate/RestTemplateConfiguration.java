package com.leaseplan.digital.config.resttemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Value("${api.weather.baseUrl}")
    private String baseUrl;

    @Value("${api.weather.version}")
    private String version;

    @Bean
    public RestTemplate weatherApiRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(baseUrl + version)
                .errorHandler(new RestTemplateErrorHandler())
                .interceptors(new RestTemplateLoggingInterceptor())
                .build();
    }
}
