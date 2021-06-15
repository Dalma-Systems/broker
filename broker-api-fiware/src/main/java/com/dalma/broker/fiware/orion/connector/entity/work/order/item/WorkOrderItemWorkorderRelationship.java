package com.dalma.broker.fiware.orion.connector.entity.work.order.item;

import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderItemWorkorderRelationship extends OrionRelationshipEntity {

	private OrionBaseAttribute<String> refWorkorder;
}
