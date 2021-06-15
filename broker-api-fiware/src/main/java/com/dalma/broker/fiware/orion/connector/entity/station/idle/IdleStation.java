package com.dalma.broker.fiware.orion.connector.entity.station.idle;

import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import com.dalma.broker.fiware.orion.connector.entity.station.Station;
import com.dalma.common.entity.EntityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdleStation extends Station {

	private static final long serialVersionUID = 7847684059242372816L;

    private OrionAttribute<String> refRobot;

    @Override
    public String getEntityType() {
        return EntityType.IDLE_STATION.getType();
    }
}
