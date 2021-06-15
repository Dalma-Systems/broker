package com.dalma.broker.service.reader;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.subscription.Subscription;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionOrionReader;
import com.dalma.contract.dto.subscription.BrokerSubscriptionSummaryOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionReader extends SubscriptionOrionReader<BrokerSubscriptionSummaryOutputDto> {

    private final ModelMapper modelMapper;

	public SubscriptionReader(OrionConnectorConfiguration config, ModelMapper modelMapper) {
		super(config);
		this.modelMapper = modelMapper;
	}

	@Override
	public BrokerSubscriptionSummaryOutputDto mapOrionEntityToEntity(Subscription entity) {
        return modelMapper.map(entity, BrokerSubscriptionSummaryOutputDto.class);
	}
}
