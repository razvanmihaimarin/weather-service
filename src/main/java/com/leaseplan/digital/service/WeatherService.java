package com.leaseplan.digital.service;

import com.leaseplan.digital.config.properties.WeatherApiProperties;
import com.leaseplan.digital.exception.WeatherApiException;
import com.leaseplan.digital.mapper.WeatherMapper;
import com.leaseplan.digital.model.api.WeatherApiResponse;
import com.leaseplan.digital.dto.WeatherResponse;
import com.leaseplan.digital.model.entity.WeatherEntity;
import com.leaseplan.digital.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeatherService {

    public static final String DATE_PATTERN = "yyyy-MM-dd_HH:00";
    public static final String WEATHER_API_URI = "/weather?q={city}&{appIdQueryParam}={appId}";

    private final WeatherRepository weatherRepository;
    private final RestTemplate weatherApiRestTemplate;
    private final RedisTemplate<String, WeatherResponse> redisTemplate;
    private final WeatherMapper weatherMapper;
    private final WeatherApiProperties weatherApiProperties;
    private final Clock clock;

    public WeatherService(WeatherRepository weatherRepository,
                          @Qualifier("weatherApiRestTemplate") RestTemplate weatherApiRestTemplate,
                          RedisTemplate<String, WeatherResponse> redisTemplate,
                          WeatherMapper weatherMapper,
                          WeatherApiProperties weatherApiProperties,
                          Clock clock) {
        this.weatherRepository = weatherRepository;
        this.weatherApiRestTemplate = weatherApiRestTemplate;
        this.redisTemplate = redisTemplate;
        this.weatherApiProperties = weatherApiProperties;
        this.weatherMapper = weatherMapper;
        this.clock = clock;
    }

    public WeatherResponse getWeatherResponse(String city) {
        String cityCacheKey = getCityCacheKey(city);
        Optional<WeatherResponse> cachedWeatherDto = Optional.ofNullable(redisTemplate.opsForValue().get(cityCacheKey));
        if (cachedWeatherDto.isPresent()) {
            return cachedWeatherDto.get();
        }

        LocalDateTime localDateTime = getCurrentUTCDateTime();
        Optional<WeatherEntity> dbWeatherEntity = weatherRepository.findByCityAndAddedDate(city, localDateTime);
        if (dbWeatherEntity.isPresent()) {
            return cacheWeatherResponse(cityCacheKey, dbWeatherEntity.get());
        }

        Optional<WeatherApiResponse> response = Optional.ofNullable(weatherApiRestTemplate.getForEntity(WEATHER_API_URI,
                WeatherApiResponse.class, getWeatherApiParameters(city)).getBody());
        if (response.isEmpty()) {
            throw new WeatherApiException("Empty response body for weather api request for city: " + city);
        }

        return persistWeatherResponse(cityCacheKey, localDateTime, response.get());
    }

    private WeatherResponse persistWeatherResponse(String cityCacheKey, LocalDateTime localDateTime,
                                                   WeatherApiResponse response) {
        WeatherEntity entityToSave = weatherMapper.toWeatherEntity(response);
        entityToSave.setAddedDate(localDateTime);
        WeatherEntity weatherEntity = weatherRepository.save(entityToSave);

        return cacheWeatherResponse(cityCacheKey, weatherEntity);
    }

    private WeatherResponse cacheWeatherResponse(String cityCacheKey, WeatherEntity weatherEntity){
        WeatherResponse weatherResponse = weatherMapper.toWeatherResponse(weatherEntity);
        redisTemplate.opsForValue().set(cityCacheKey, weatherResponse,
                weatherApiProperties.getCacheTtlInHours(), TimeUnit.HOURS);

        return weatherResponse;
    }

    private Map<String, String> getWeatherApiParameters(String city) {
        Map<String, String> params = new HashMap<>();
        params.put("city", city);
        params.put("appIdQueryParam", weatherApiProperties.getAppIdQueryParam());
        params.put("appId", weatherApiProperties.getAppId());

        return params;
    }

    private String getCityCacheKey(String city) {
        String formattedDate = getCurrentUTCDateTime().format(DateTimeFormatter.ofPattern(DATE_PATTERN));

        return city.toUpperCase() + "_" + formattedDate;
    }

    private LocalDateTime getCurrentUTCDateTime() {
        return LocalDateTime.ofInstant(clock.instant(), ZoneId.of("UTC")).withMinute(0).withSecond(0).withNano(0);
    }

}
