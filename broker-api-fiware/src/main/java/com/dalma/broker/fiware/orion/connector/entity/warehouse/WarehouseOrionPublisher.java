package com.dalma.broker.fiware.orion.connector.entity.warehouse;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class WarehouseOrionPublisher<T> extends DefaultOrionPublisher<T, Warehouse> {

    public WarehouseOrionPublisher(OrionConnectorConfiguration config) {
        super(config, Warehouse.class);
    }
}
