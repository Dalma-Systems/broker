package com.dalma.broker.fiware.orion.connector.entity.work.order.item;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class WorkOrderItemOrionPublisher<T> extends DefaultOrionPublisher<T, WorkOrderItem> {

    public WorkOrderItemOrionPublisher(OrionConnectorConfiguration config) {
        super(config, WorkOrderItem.class);
    }
}
