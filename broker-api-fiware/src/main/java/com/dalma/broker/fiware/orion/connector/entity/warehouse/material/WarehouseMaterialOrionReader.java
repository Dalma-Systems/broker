package com.dalma.broker.fiware.orion.connector.entity.warehouse.material;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class WarehouseMaterialOrionReader<T> extends DefaultOrionReader<T, WarehouseMaterial> {

    public WarehouseMaterialOrionReader(OrionConnectorConfiguration config) {
        super(config, WarehouseMaterial.class);
    }
}
