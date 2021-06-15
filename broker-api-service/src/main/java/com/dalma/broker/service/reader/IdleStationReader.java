package com.dalma.broker.service.reader;

import com.dalma.broker.contract.dto.station.idle.BrokerIdleStationSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStation;
import com.dalma.broker.fiware.orion.connector.entity.station.idle.IdleStationOrionReader;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class IdleStationReader extends IdleStationOrionReader<BrokerIdleStationSummaryOutputDto> {

	private final ModelMapper modelMapper;

	public IdleStationReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BrokerIdleStationSummaryOutputDto mapOrionEntityToEntity(IdleStation entity) {
		return modelMapper.map(entity, BrokerIdleStationSummaryOutputDto.class);
	}
}
