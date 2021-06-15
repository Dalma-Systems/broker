package com.dalma.broker.contract.dto.station.idle;

import com.dalma.contract.dto.base.BaseOrionAttributeOutputDto;
import com.dalma.contract.dto.station.BrokerStationSummaryOutputDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerIdleStationSummaryOutputDto extends BrokerStationSummaryOutputDto {
	private BaseOrionAttributeOutputDto<String> robotId; 
}
