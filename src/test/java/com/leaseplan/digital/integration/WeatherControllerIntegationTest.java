package com.leaseplan.digital.integration;


import com.leaseplan.digital.model.dto.WeatherResponse;
import com.leaseplan.digital.model.entity.WeatherEntity;
import com.leaseplan.digital.repository.WeatherRepository;
import com.leaseplan.digital.service.WeatherService;
import com.playtika.test.postgresql.EmbeddedPostgreSQLBootstrapConfiguration;
import com.playtika.test.redis.EmbeddedRedisDependenciesAutoConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({EmbeddedPostgreSQLBootstrapConfiguration.class, EmbeddedRedisDependenciesAutoConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WeatherControllerIntegationTest {

    public static final String TEST_CITY = "Dublin";
    public static final String DATE_PATTERN = "yyyy-MM-dd_HH:00";

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private WeatherRepository weatherRepository;
    @Autowired
    private RedisTemplate<String, WeatherResponse> redisTemplate;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Clock clock;

    @Before
    public void setup() {
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(1621057996));
    }

    @Test
    public void givenGivenCorrectCity_whenGetWeather_thenPersistAndReturnResponse() throws Exception {
        mockMvc.perform(get("/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", TEST_CITY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.temperature").exists())
                .andExpect(jsonPath("$.country").exists())
                .andExpect(jsonPath("$.city").exists())
                .andExpect(jsonPath("$.city").value(TEST_CITY));

        LocalDateTime dateTime = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC).withMinute(0).withSecond(0).withNano(0);
        Optional<WeatherEntity> weatherEntity = weatherRepository.findByCityAndAddedDate(TEST_CITY, dateTime);
        assertThat(weatherEntity.get().getCity(), is(equalTo(TEST_CITY)));
        assertThat(weatherEntity.get().getCity(), is(equalTo(TEST_CITY)));
        assertThat(weatherEntity.get().getId(), is(equalTo(1)));

        WeatherResponse weatherResponse = redisTemplate.opsForValue().get(
                TEST_CITY.toUpperCase() + "_" + dateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        assertThat(weatherResponse.getCity(), is(equalTo(TEST_CITY)));

    }

    @Test
    public void givenInvalidCityMinSize_whenGetWeather_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", "a"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(),
                containsString("weather.city: size must be between 3 and 25"));
    }

    @Test
    public void givenInvalidCityMaxSize_whenGetWeather_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(),
                containsString("weather.city: size must be between 3 and 25"));
    }

    @Test
    public void givenValidCityNotFoundInApi_whenGetWeather_thenReturns400() throws Exception {
        MvcResult result = mockMvc.perform(get("/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .param("city", "Dublinqaz"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(),
                containsString("Could not find the provided city."));
    }
}
