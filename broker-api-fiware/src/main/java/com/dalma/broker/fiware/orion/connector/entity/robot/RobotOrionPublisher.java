package com.dalma.broker.fiware.orion.connector.entity.robot;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.publisher.DefaultOrionPublisher;

public abstract class RobotOrionPublisher<T> extends DefaultOrionPublisher<T, Robot> {

    public RobotOrionPublisher(OrionConnectorConfiguration config) {
        super(config, Robot.class);
    }
}
