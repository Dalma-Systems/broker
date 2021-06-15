package com.dalma.broker.service.publisher;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationship;
import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.subscription.Subscription;
import com.dalma.broker.fiware.orion.connector.entity.subscription.SubscriptionOrionPublisher;
import com.dalma.contract.dto.subscription.BrokerSubscriptionInputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionPublisher extends SubscriptionOrionPublisher<BrokerSubscriptionInputDto> {

    private final ModelMapper modelMapper;

    public SubscriptionPublisher(OrionConnectorConfiguration config, ModelMapper modelMapper) {
        super(config);
        this.modelMapper = modelMapper;
    }

    @Override
    public Subscription mapEntityToOrionEntity(BrokerSubscriptionInputDto entity) {
        return modelMapper.map(entity, Subscription.class);
    }

    @Override
    public OrionRelationship<OrionRelationshipEntity> mapEntityToOrionRelationshipEntity(Subscription orionEntity,
                                                                                         List<String> relatedIds, 
                                                                                         String contextId) {
        return null;
    }

    @Override
    protected Subscription mapEntityIdAndDate(Subscription mapEntityToOrionEntity) {
        return mapEntityToOrionEntity;
    }
}
