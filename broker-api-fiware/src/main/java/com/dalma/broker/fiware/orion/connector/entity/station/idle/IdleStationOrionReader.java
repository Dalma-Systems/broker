package com.dalma.broker.fiware.orion.connector.entity.station.idle;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class IdleStationOrionReader<T> extends DefaultOrionReader<T, IdleStation> {

    public IdleStationOrionReader(OrionConnectorConfiguration config) {
        super(config, IdleStation.class);
    }
}
