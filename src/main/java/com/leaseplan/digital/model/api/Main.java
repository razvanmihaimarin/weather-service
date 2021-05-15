
package com.leaseplan.digital.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Main {

    private Double temp;
    private Integer pressure;
    private Integer humidity;
    @JsonProperty("temp_min")
    private Double tempMin;
    @JsonProperty("temp_max")
    private Double tempMax;

}
