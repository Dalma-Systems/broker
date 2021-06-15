package com.dalma.broker.fiware.orion.connector.entity.work.order.item;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class WorkOrderItemOrionReader<T> extends DefaultOrionReader<T, WorkOrderItem> {

    public WorkOrderItemOrionReader(OrionConnectorConfiguration config) {
        super(config, WorkOrderItem.class);
    }
}
