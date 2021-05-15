package com.leaseplan.digital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class WeatherApplicationConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
