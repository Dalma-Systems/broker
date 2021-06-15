package com.dalma.broker.fiware.orion.connector.entity.station.work.field;

import lombok.Getter;

@Getter
public enum WorkStationField {
	RELATIONSHIP_WORKSTATION("refWorkstation"),
	ERP_EXTERNAL_ID("erpId"),
	;
	
	private String field;
	
	private WorkStationField(String field) {
		this.field = field;
	}
}
