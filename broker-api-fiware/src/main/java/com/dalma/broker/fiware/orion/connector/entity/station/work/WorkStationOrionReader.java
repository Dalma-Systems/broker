package com.dalma.broker.fiware.orion.connector.entity.station.work;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class WorkStationOrionReader<T> extends DefaultOrionReader<T, WorkStation> {

    public WorkStationOrionReader(OrionConnectorConfiguration config) {
        super(config, WorkStation.class);
    }
}
