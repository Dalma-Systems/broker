package com.dalma.broker.fiware.orion.connector.entity.station.idle;

import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdleStationRelationship extends OrionRelationshipEntity {

	private OrionBaseAttribute<String> refRobot;
}
