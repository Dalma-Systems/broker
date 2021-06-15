package com.dalma.broker.fiware.orion.connector.entity.warehouse.material;

import com.dalma.broker.fiware.orion.connector.entity.OrionRelationshipEntity;
import com.dalma.broker.fiware.orion.connector.entity.attribute.OrionBaseAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseMaterialRelationship extends OrionRelationshipEntity {

	private OrionBaseAttribute<String> refWarehouse;
}
