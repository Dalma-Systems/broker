package com.dalma.broker.contract.dto.station.work;

import com.dalma.contract.dto.station.BrokerStationInputDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BrokerWorkStationInputDto extends BrokerStationInputDto {
	private String erpId;
}
