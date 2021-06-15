package com.dalma.broker.fiware.orion.connector.entity.subscription;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class SubscriptionOrionReader<T> extends DefaultOrionReader<T, Subscription> {

    public SubscriptionOrionReader(OrionConnectorConfiguration config) {
        super(config, Subscription.class);
    }
}
