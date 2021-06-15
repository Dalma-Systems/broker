package com.dalma.broker.service.reader;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrder;
import com.dalma.broker.fiware.orion.connector.entity.work.order.WorkOrderOrionReader;
import com.dalma.contract.dto.work.order.BrokerWorkOrderSummaryOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderReader extends WorkOrderOrionReader<BrokerWorkOrderSummaryOutputDto> {

	private final ModelMapper modelMapper;

	public WorkOrderReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BrokerWorkOrderSummaryOutputDto mapOrionEntityToEntity(WorkOrder entity) {
		return modelMapper.map(entity, BrokerWorkOrderSummaryOutputDto.class);
	}
}
