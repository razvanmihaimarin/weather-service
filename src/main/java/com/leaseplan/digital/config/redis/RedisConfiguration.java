package com.leaseplan.digital.config.redis;

import com.leaseplan.digital.model.dto.WeatherResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, WeatherResponse> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, WeatherResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
