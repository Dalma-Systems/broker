package com.dalma.broker.contract.dto.station.idle;

import com.dalma.broker.contract.dto.station.BrokerStationOutputDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BrokerIdleStationOutputDto extends BrokerStationOutputDto {

}
