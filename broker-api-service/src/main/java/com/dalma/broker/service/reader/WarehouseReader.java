package com.dalma.broker.service.reader;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.Warehouse;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.WarehouseOrionReader;
import com.dalma.contract.dto.warehouse.BrokerWarehouseSummaryOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WarehouseReader extends WarehouseOrionReader<BrokerWarehouseSummaryOutputDto> {

	private final ModelMapper modelMapper;

	public WarehouseReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BrokerWarehouseSummaryOutputDto mapOrionEntityToEntity(Warehouse entity) {
		return modelMapper.map(entity, BrokerWarehouseSummaryOutputDto.class);
	}
}
