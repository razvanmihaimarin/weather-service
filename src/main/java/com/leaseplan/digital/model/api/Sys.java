
package com.leaseplan.digital.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Sys {

    private Integer type;
    private Integer id;
    private Double message;
    private String country;
    private Integer sunrise;
    private Integer sunset;

}
