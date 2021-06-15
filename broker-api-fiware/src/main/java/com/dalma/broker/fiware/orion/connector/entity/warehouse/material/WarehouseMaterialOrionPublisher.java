package com.dalma.broker.fiware.orion.connector.entity.warehouse.material;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class WarehouseMaterialOrionPublisher<T> extends DefaultOrionPublisher<T, WarehouseMaterial> {

    public WarehouseMaterialOrionPublisher(OrionConnectorConfiguration config) {
        super(config, WarehouseMaterial.class);
    }
}
