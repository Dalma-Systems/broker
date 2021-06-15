package com.dalma.broker.fiware.orion.connector.entity.subscription;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class SubscriptionOrionPublisher<T> extends DefaultOrionPublisher<T, Subscription> {

    public SubscriptionOrionPublisher(OrionConnectorConfiguration config) {
        super(config, Subscription.class);
    }
}
