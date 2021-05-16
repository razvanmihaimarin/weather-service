package com.leaseplan.digital.mapper;

import com.leaseplan.digital.model.api.WeatherApiResponse;
import com.leaseplan.digital.dto.WeatherResponse;
import com.leaseplan.digital.model.entity.WeatherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mappings({
            @Mapping(target="city", source="weatherApiResponse.name"),
            @Mapping(target="country", source="weatherApiResponse.sys.country"),
            @Mapping(target="temperature", source="weatherApiResponse.main.temp")
    })
    WeatherEntity toWeatherEntity(WeatherApiResponse weatherApiResponse);

    WeatherResponse toWeatherResponse(WeatherEntity weatherEntity);

}
