package com.leaseplan.digital.controller;

import com.leaseplan.digital.dto.WeatherResponse;
import com.leaseplan.digital.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

@RestController
@Validated
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherResponse> weather(@RequestParam @Size(min = 3, max = 25) String city) {
        log.info("Received weather request for city: {}", city);

        return ResponseEntity.ok(weatherService.getWeatherResponse(city));
    }

}
