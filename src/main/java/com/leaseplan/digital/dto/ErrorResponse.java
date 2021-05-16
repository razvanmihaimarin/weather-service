package com.leaseplan.digital.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
}
