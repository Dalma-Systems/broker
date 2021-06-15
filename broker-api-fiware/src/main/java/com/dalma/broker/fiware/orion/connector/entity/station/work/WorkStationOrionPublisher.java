package com.dalma.broker.fiware.orion.connector.entity.station.work;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class WorkStationOrionPublisher<T> extends DefaultOrionPublisher<T, WorkStation> {

    public WorkStationOrionPublisher(OrionConnectorConfiguration config) {
        super(config, WorkStation.class);
    }
}
