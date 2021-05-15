package com.leaseplan.digital.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
@NoArgsConstructor
@Setter
@Getter
public class WeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String city;
    private String country;
    private Double temperature;
    @Column(name = "added_date")
    private LocalDateTime addedDate;

}
