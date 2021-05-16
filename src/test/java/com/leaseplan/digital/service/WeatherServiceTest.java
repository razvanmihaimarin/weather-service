package com.leaseplan.digital.service;

import com.leaseplan.digital.config.properties.WeatherApiProperties;
import com.leaseplan.digital.exception.WeatherApiException;
import com.leaseplan.digital.mapper.WeatherMapper;
import com.leaseplan.digital.model.api.WeatherApiResponse;
import com.leaseplan.digital.dto.WeatherResponse;
import com.leaseplan.digital.model.entity.WeatherEntity;
import com.leaseplan.digital.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    public static final String CITY = "Cairo";
    public static final String COUNTRY = "Egipt";
    public static final double TEMPERATURE = 340D;
    public static final String CACHE_KEY = "TESTCITY_2021-05-12_05:00";
    public static final String TEST_CITY = "testCity";

    @Mock
    private WeatherRepository weatherRepository;
    @Mock
    private RestTemplate weatherApiRestTemplate;
    @Mock
    private RedisTemplate<String, WeatherResponse> redisTemplate;
    @Mock
    private WeatherMapper weatherMapper;
    @Mock
    private WeatherApiProperties weatherApiProperties;
    @Mock
    private Clock clock;
    @Mock
    private ValueOperations valueOperations;


    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(1620795623));
    }

    @Test
    public void givenCity_whenGetWeatherDtoPresentInCache_thenReturnFromCache() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(buildWeatherDto());

        // when
        WeatherResponse resultWeatherResponse = weatherService.getWeatherResponse(TEST_CITY);

        // then
        assertEquals(CITY, resultWeatherResponse.getCity());
        assertEquals(COUNTRY, resultWeatherResponse.getCountry());
    }

    @Test
    public void givenCity_whenGetWeatherDtoNotPresentInCachePresentInDb_thenReturnFromDb() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
        when(weatherRepository.findByCityAndAddedDate(anyString(), any())).thenReturn(Optional.of(buildWeatherEntity()));
        WeatherResponse weatherResponse = buildWeatherDto();
        when(weatherMapper.toWeatherResponse(any())).thenReturn(weatherResponse);

        // when
        WeatherResponse resultWeatherResponse = weatherService.getWeatherResponse(TEST_CITY);

        // then
        assertEquals(CITY, resultWeatherResponse.getCity());
        assertEquals(COUNTRY, resultWeatherResponse.getCountry());
    }

    @Test
    public void givenCity_whenGetWeatherDtoNotPresentInCacheAndResponseNull_thenReturnThrowException() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
        when(weatherApiRestTemplate.getForEntity(anyString(),
                any(), anyMap())).thenReturn(new ResponseEntity(null, HttpStatus.OK));
        when(weatherRepository.findByCityAndAddedDate(anyString(), any())).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(WeatherApiException.class, () -> {
            WeatherResponse weatherDtoOptional = weatherService.getWeatherResponse(TEST_CITY);
        });

        String expectedMessage = "Empty response body for weather api request for city: " + TEST_CITY;
        String actualMessage = exception.getMessage();
        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void givenCity_whenGetWeatherDtoNotPresentInCacheOrDb_thenCallEndpoint() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
        when(weatherApiRestTemplate.getForEntity(anyString(),
                any(), anyMap())).thenReturn(new ResponseEntity(new WeatherApiResponse(), HttpStatus.OK));
        when(weatherRepository.findByCityAndAddedDate(anyString(), any())).thenReturn(Optional.empty());
        WeatherEntity weatherEntity = buildWeatherEntity();
        when(weatherRepository.save(any())).thenReturn(weatherEntity);
        WeatherResponse weatherResponse = buildWeatherDto();
        when(weatherMapper.toWeatherResponse(any())).thenReturn(weatherResponse);
        when(weatherMapper.toWeatherEntity(any())).thenReturn(weatherEntity);
        doNothing().when(valueOperations).set(CACHE_KEY, weatherResponse, 0L, TimeUnit.HOURS);

        // when
        WeatherResponse resultWeatherResponse = weatherService.getWeatherResponse(TEST_CITY);

        // then
        assertEquals(CITY, resultWeatherResponse.getCity());
        assertEquals(COUNTRY, resultWeatherResponse.getCountry());
        assertEquals(Double.valueOf(TEMPERATURE), resultWeatherResponse.getTemperature());
    }


    private WeatherResponse buildWeatherDto() {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setId(1);
        weatherResponse.setCity(CITY);
        weatherResponse.setCountry(COUNTRY);
        weatherResponse.setTemperature(TEMPERATURE);

        return weatherResponse;
    }

    private WeatherEntity buildWeatherEntity() {
        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setId(1);
        weatherEntity.setCity(CITY);
        weatherEntity.setCountry(COUNTRY);
        weatherEntity.setTemperature(TEMPERATURE);

        return weatherEntity;
    }
}
