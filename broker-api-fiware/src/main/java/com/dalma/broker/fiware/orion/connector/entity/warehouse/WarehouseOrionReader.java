package com.dalma.broker.fiware.orion.connector.entity.warehouse;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class WarehouseOrionReader<T> extends DefaultOrionReader<T, Warehouse> {

    public WarehouseOrionReader(OrionConnectorConfiguration config) {
        super(config, Warehouse.class);
    }
}
