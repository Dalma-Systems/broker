package com.dalma.broker.fiware.orion.connector.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrionRelationship<T extends OrionRelationshipEntity> {
	private String actionType = "APPEND";
	private List<T> entities;
}
