package com.leaseplan.digital.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.weather")
@Setter
@Getter
public class WeatherApiProperties {

    private String appId;
    private String appIdQueryParam;
    private String baseUrl;
    private String version;
    private Integer cacheTtlInHours;
}
