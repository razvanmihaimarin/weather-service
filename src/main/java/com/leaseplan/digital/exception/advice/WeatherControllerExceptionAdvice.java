package com.leaseplan.digital.exception.advice;

import com.leaseplan.digital.exception.WeatherApiException;
import com.leaseplan.digital.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class WeatherControllerExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleSizeException(ResponseStatusException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.builder()
                        .message(buildErrorMessage(exception))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(exception.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<ErrorResponse> handleWeatherApiException(WeatherApiException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .message(exception.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    private String buildErrorMessage(ResponseStatusException exception) {
        if (exception.getStatus().equals(HttpStatus.BAD_REQUEST)) {
            return "Could not find the provided city.";
        }
        return "There has been an error while processing your request.";
    }

}
