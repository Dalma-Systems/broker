package com.dalma.broker.service.reader;

import com.dalma.broker.contract.dto.base.BaseSummaryOutputDto;
import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItem;
import com.dalma.broker.fiware.orion.connector.entity.work.order.item.WorkOrderItemOrionReader;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderItemReader extends WorkOrderItemOrionReader<BaseSummaryOutputDto> {

	private final ModelMapper modelMapper;

	public WorkOrderItemReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BaseSummaryOutputDto mapOrionEntityToEntity(WorkOrderItem entity) {
		return modelMapper.map(entity, BaseSummaryOutputDto.class);
	}
}
