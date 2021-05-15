package com.leaseplan.digital.repository;

import com.leaseplan.digital.model.entity.WeatherEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {

    Optional<WeatherEntity> findByCityAndAddedDate(String city, LocalDateTime addedDate);
}
