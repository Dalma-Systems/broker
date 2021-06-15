package com.dalma.broker.fiware.orion.connector.entity.robot;

import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class RobotWarehouseMaterialRelationship extends OrionRelationshipEntity {

	private OrionBaseAttribute<ArrayList<String>> refPayload;
}
