
package com.leaseplan.digital.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Coord {

    private Double lon;
    private Double lat;

}
