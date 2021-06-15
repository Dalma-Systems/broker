package com.dalma.broker.fiware.orion.connector.entity.work.order;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class WorkOrderOrionReader<T> extends DefaultOrionReader<T, WorkOrder> {

    public WorkOrderOrionReader(OrionConnectorConfiguration config) {
        super(config, WorkOrder.class);
    }
}
