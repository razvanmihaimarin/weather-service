package com.leaseplan.digital.model.dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse implements Serializable {

    private Integer id;
    private String city;
    private String country;
    private Double temperature;

}
