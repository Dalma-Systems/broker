package com.dalma.broker.fiware.orion.connector.entity.work.order;

import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderWorkStationRelationship extends OrionRelationshipEntity {

	private OrionBaseAttribute<String> refWorkstation;
}
