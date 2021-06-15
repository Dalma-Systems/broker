package com.dalma.broker.fiware.orion.connector.entity.work.order;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class WorkOrderOrionPublisher<T> extends DefaultOrionPublisher<T, WorkOrder> {

    public WorkOrderOrionPublisher(OrionConnectorConfiguration config) {
        super(config, WorkOrder.class);
    }
}
