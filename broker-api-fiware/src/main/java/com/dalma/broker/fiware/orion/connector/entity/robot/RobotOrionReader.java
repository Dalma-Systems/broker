package com.dalma.broker.fiware.orion.connector.entity.robot;

import com.dalma.broker.fiware.orion.connector.OrionConnectorConfiguration;
import com.dalma.broker.fiware.orion.reader.DefaultOrionReader;

public abstract class RobotOrionReader<T> extends DefaultOrionReader<T, Robot> {

    public RobotOrionReader(OrionConnectorConfiguration config) {
        super(config, Robot.class);
    }
}
