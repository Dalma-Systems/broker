package com.dalma.broker.contract.dto.station;

import com.dalma.broker.contract.dto.base.BaseOutputDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
public class BrokerStationOutputDto extends BaseOutputDto {

    @NotBlank
    private String orionId;
}
