package com.dalma.broker.fiware.orion.connector.entity.warehouse.field;

import lombok.Getter;

@Getter
public enum WarehouseField {
	RELATIONSHIP_WAREHOUSE("refWarehouse"),
	ERP_EXTERNAL_ID("erpId"),
	;
	
	private String field;
	
	private WarehouseField(String field) {
		this.field = field;
	}
}
