package com.dalma.broker.service.reader;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.station.work.WorkStation;
import com.dalma.broker.fiware.orion.connector.entity.station.work.WorkStationOrionReader;
import com.dalma.contract.dto.station.work.BrokerWorkStationSummaryOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkStationReader extends WorkStationOrionReader<BrokerWorkStationSummaryOutputDto> {

	private final ModelMapper modelMapper;

	public WorkStationReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BrokerWorkStationSummaryOutputDto mapOrionEntityToEntity(WorkStation entity) {
		return modelMapper.map(entity, BrokerWorkStationSummaryOutputDto.class);
	}
}
