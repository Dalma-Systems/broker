package com.dalma.broker.fiware.orion.connector.entity.station.idle;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class IdleStationOrionPublisher<T> extends DefaultOrionPublisher<T, IdleStation> {

    public IdleStationOrionPublisher(OrionConnectorConfiguration config) {
        super(config, IdleStation.class);
    }
}
