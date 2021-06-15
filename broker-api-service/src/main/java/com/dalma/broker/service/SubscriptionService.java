package com.dalma.broker.service;

import com.dalma.broker.contract.dto.subscription.BrokerSubscriptionOutputDto;
import com.dalma.broker.fiware.orion.connector.entity.subscription.Subscription;
import com.dalma.broker.service.publisher.SubscriptionPublisher;
import com.dalma.broker.service.reader.SubscriptionReader;
import com.dalma.contract.dto.subscription.BrokerSubscriptionInputDto;
import com.dalma.contract.dto.subscription.BrokerSubscriptionSummaryOutputDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    private final SubscriptionPublisher subscriptionPublisher;
    private final SubscriptionReader subscriptionReader;
    private final ModelMapper modelMapper;

    public SubscriptionService(SubscriptionPublisher subscriptionPublisher, SubscriptionReader subscriptionReader, ModelMapper modelMapper) {
        this.subscriptionPublisher = subscriptionPublisher;
        this.subscriptionReader = subscriptionReader;
        this.modelMapper = modelMapper;
    }

    public BrokerSubscriptionOutputDto createSubscription(BrokerSubscriptionInputDto subscriptionInputDto) {
        Subscription subscription = subscriptionPublisher.subscribe(subscriptionInputDto);
        return mapOrionEntityToEntity(subscription);
    }

    public void deleteSubscription(String id) {
        subscriptionPublisher.deleteSubscription(id);
    }

    private BrokerSubscriptionOutputDto mapOrionEntityToEntity(Subscription orionEntity) {
        return new BrokerSubscriptionOutputDto(orionEntity.getId());
    }

	public List<BrokerSubscriptionSummaryOutputDto> getAllSubscriptions() {
		List<Subscription> subscriptions = subscriptionReader.readOrionSubscriptionList();
		if (subscriptions == null) {
			return Collections.emptyList();
		}
		return subscriptions.stream().map(subscription -> modelMapper.map(subscription, BrokerSubscriptionSummaryOutputDto.class)).collect(Collectors.toList());
	}

	public List<BrokerSubscriptionSummaryOutputDto> getSubscriptionByConditionAttribute(String attr) {
		List<BrokerSubscriptionSummaryOutputDto> subscriptions = getAllSubscriptions();
		return subscriptions.stream().filter(subscription -> subscription.getAttributes().contains(attr)).collect(Collectors.toList());
	}

	public BrokerSubscriptionSummaryOutputDto getSubscriptionById(String id) {
		Subscription subscription = subscriptionReader.readOrionSubscription(id);
		return modelMapper.map(subscription, BrokerSubscriptionSummaryOutputDto.class);
	}
}
