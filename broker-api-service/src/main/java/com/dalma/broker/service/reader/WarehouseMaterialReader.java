package com.dalma.broker.service.reader;

import com.dalma.broker.contract.dto.warehouse.material.BrokerWarehouseMaterialSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterial;
import com.dalma.broker.fiware.orion.connector.entity.warehouse.material.WarehouseMaterialOrionReader;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMaterialReader extends WarehouseMaterialOrionReader<BrokerWarehouseMaterialSummaryOutputDto> {

	private final ModelMapper modelMapper;

	public WarehouseMaterialReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BrokerWarehouseMaterialSummaryOutputDto mapOrionEntityToEntity(WarehouseMaterial entity) {
		return modelMapper.map(entity, BrokerWarehouseMaterialSummaryOutputDto.class);
	}
}
