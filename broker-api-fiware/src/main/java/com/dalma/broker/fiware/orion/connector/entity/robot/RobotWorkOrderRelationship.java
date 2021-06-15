package com.dalma.broker.fiware.orion.connector.entity.robot;

import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RobotWorkOrderRelationship extends OrionRelationshipEntity {

	private OrionAttribute<String> refWorkOrder;
}
