package com.dalma.broker.fiware.orion.connector.entity;

import lombok.Getter;

@Getter
public enum OrionEntityType {
    TEXT("Text"), NUMBER("Number"), DATE_TIME("DateTime"), POINT("Point"), GEO_JSON("geo:json"),
    RELATIONSHIP("Relationship"), BOOLEAN("Boolean"), DOUBLE("Double"),
    ;

    private String type;

    OrionEntityType(String type) {
        this.type = type;
    }
}
